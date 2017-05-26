package com.template.state

import com.template.contract.Yo
import net.corda.core.contracts.*
import net.corda.core.crypto.CompositeKey
import net.corda.core.crypto.Party
import java.security.PublicKey

data class State(val origin: Party,
                     val target: Party,
                     val yo: String = "Yo!",
                     override val linearId: UniqueIdentifier = UniqueIdentifier()): LinearState {
        override val participants get() = listOf(target.owningKey)
        override val contract get() = Yo()
        override fun isRelevant(ourKeys: Set<PublicKey>) = ourKeys.intersect(participants).isNotEmpty()
        override fun toString() = "${origin.name}: $yo"
    }