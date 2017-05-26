package com.template.flow

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.crypto.Party
import net.corda.core.flows.FlowLogic
import net.corda.core.messaging.CordaRPCOps
import javax.ws.rs.GET
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import net.corda.client.rpc.notUsed
import net.corda.core.contracts.*
import net.corda.core.crypto.SecureHash
import net.corda.core.getOrThrow
import net.corda.core.node.CordaPluginRegistry
import net.corda.core.node.PluginServiceHub
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.ProgressTracker
import net.corda.flows.FinalityFlow
import rx.Observable
import java.security.PublicKey
import java.util.function.Function
import javax.ws.rs.QueryParam
import com.template.flow.YoFlow
import com.template.state.State
import com.template.contract.Yo

class YoFlow(val target: Party): FlowLogic<SignedTransaction>() {

    override val progressTracker: ProgressTracker = YoFlow.tracker()

    companion object {
        object CREATING : ProgressTracker.Step("Creating a new Yo!")
        object VERIFYING : ProgressTracker.Step("Verifying the Yo!")
        object SENDING : ProgressTracker.Step("Sending the Yo!")
        fun tracker() = ProgressTracker(CREATING, VERIFYING, SENDING)
    }

    @Suspendable
    override fun call(): SignedTransaction {
        val me = serviceHub.myInfo.legalIdentity
        val notary = serviceHub.networkMapCache.notaryNodes.single().notaryIdentity

        progressTracker.currentStep = CREATING
        val signedYo = TransactionType.General.Builder(notary)
                .withItems(State(me, target), Command(Yo.Send(), listOf(me.owningKey)))
                .signWith(serviceHub.legalIdentityKey)
                .toSignedTransaction(true)

        progressTracker.currentStep = VERIFYING
        signedYo.tx.toLedgerTransaction(serviceHub).verify()

        progressTracker.currentStep = SENDING
        return subFlow(FinalityFlow(signedYo, setOf(target))).single()
    }
}