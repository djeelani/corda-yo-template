package com.template.contract

import net.corda.core.contracts.Contract
import net.corda.core.contracts.TransactionForContract
import net.corda.core.crypto.SecureHash
import co.paralleluniverse.fibers.Suspendable
import net.corda.client.rpc.notUsed
import net.corda.core.contracts.*
import net.corda.core.crypto.Party
import net.corda.core.flows.FlowLogic
import net.corda.core.getOrThrow
import net.corda.core.messaging.CordaRPCOps
import net.corda.core.node.CordaPluginRegistry
import net.corda.core.node.PluginServiceHub
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.ProgressTracker
import net.corda.flows.FinalityFlow
import rx.Observable
import java.security.PublicKey
import java.util.function.Function
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import com.template.state.State

/**
 * Define your contract here.
 */
class Yo : Contract {
    // Command.
    class Send : TypeOnlyCommandData()

    // Legal prose.
    override val legalContractReference: SecureHash = SecureHash.sha256("Yo!")

    // Contract code.
    override fun verify(tx: TransactionForContract) = requireThat {
        val command = tx.commands.requireSingleCommand<Send>()
        "There can be no inputs when Yo'ing other parties." using (tx.inputs.isEmpty())
        "There must be one output: The Yo!" using (tx.outputs.size == 1)
        val yo = tx.outputs.single() as State
        "No sending Yo's to yourself!" using (yo.target != yo.origin)
        "The Yo! must be signed by the sender." using (yo.origin.owningKey == command.signers.single())
}
}
