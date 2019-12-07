package com.example.cupid.model

import android.net.MacAddress
import com.example.cupid.model.domain.Message
import com.example.cupid.model.observer.AccountObserver
import com.example.cupid.model.observer.DomainObserver
import com.example.cupid.model.observer.MessageObserver
import com.example.cupid.model.repository.AccountRepository
import com.example.cupid.model.repository.MessageRepository

class DataAccessLayer (
    private val accountRepository: AccountRepository,
    private val messageRepository: MessageRepository
) {

    private val observers = mutableListOf<DomainObserver>()

    fun register(observer: DomainObserver) = observers.add(observer)

    fun unregister(observer: DomainObserver) = observers.remove(observer)

    fun performLogin(name: String,
                     age : Int,
                     avatarId : Int,
                     photoPath : String,
                     bio : String,
                     macAddress: MacAddress) {
        // TODO: Do login
        notify(AccountObserver::accountLoggedIn)
    }

    fun updateChatroom() {

    }

    private fun notify(action: (AccountObserver) -> Unit) {
        observers.filterIsInstance<AccountObserver>().onEach { action(it) }
    }


    fun getCurrentAccount() = accountRepository.account

}