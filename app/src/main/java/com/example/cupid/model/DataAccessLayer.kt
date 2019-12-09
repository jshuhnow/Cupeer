package com.example.cupid.model

import android.net.MacAddress
import com.example.cupid.model.domain.Account
import com.example.cupid.model.domain.Answer
import com.example.cupid.model.domain.Message
import com.example.cupid.model.domain.Question
import com.example.cupid.model.observer.AccountObserver
import com.example.cupid.model.observer.DomainObserver
import com.example.cupid.model.observer.MessageObserver
import com.example.cupid.model.observer.QuestionObserver
import com.example.cupid.model.repository.AccountRepository
import com.example.cupid.model.repository.MessageRepository
import com.example.cupid.model.repository.QuestionRepository

class DataAccessLayer (
    private val accountRepository: AccountRepository,
    private val questionRepository: QuestionRepository,
    private val messageRepository: MessageRepository
) {

    private val observers = mutableListOf<DomainObserver>()

    fun register(observer: DomainObserver) = observers.add(observer)

    fun unregister(observer: DomainObserver) = observers.remove(observer)

    private fun notify(action: (AccountObserver) -> Unit) {
        observers.filterIsInstance<AccountObserver>().onEach { action(it) }
    }

    fun getUserName() = accountRepository.userAccount!!.name
    fun getUserAccount() = accountRepository.userAccount
    fun getPartnerAccount() = accountRepository.partnerAccount

    fun updateUserAccount(avartarId: Int,
                          name : String) {
        accountRepository.userAccount = Account(name=name, avatarId= avartarId)
        notify(AccountObserver::userAccountUpdated)
    }

    fun updatePartnerAccount(avartarId : Int,
                      name : String) {
        accountRepository.partnerAccount = Account(name=name, avatarId=avartarId)
    }

    fun updateUserAnswer(questionId : Int, answerId : Int) {
        accountRepository.userAccount!!.answers.add(Answer(questionId, answerId))
    }

    fun updatePartnerAnswer(questionId: Int, answerId : Int) {
        accountRepository.partnerAccount!!.answers.add(Answer(questionId, answerId))
    }

    fun getUserAnswers() : ArrayList<Answer>{
        return accountRepository.userAccount!!.answers
    }

    fun getPartnerAnswers() : ArrayList<Answer>{
        return accountRepository.partnerAccount!!.answers
    }

    fun getQuestions() = questionRepository.questions

    fun addQuestions(questionText : String, choices : ArrayList<String>) {
        questionRepository.questions.add(Question(
            questionText = questionText,
            choices = choices,
            questionId = questionRepository.questions.size
        ))
    }
    fun setQuestions(questions : ArrayList<Question>) {
        questionRepository.questions = questions
    }

    fun getMessages() : ArrayList<Message> {
        return messageRepository.messages // TODO
    }


}