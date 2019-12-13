package com.example.cupid.model

import com.example.cupid.model.repository.AccountRepository
import com.example.cupid.model.repository.MessageRepository
import com.example.cupid.model.repository.QuestionRepository

object QuestionBankModule {

    val questionBank : ArrayList<Pair<String, ArrayList<String>>> =
        arrayListOf(
            Pair("You win a lottery! What do you do with the money?",
            arrayListOf(
                "Spend it now!",
                "Better save it.",
                "Give it away.",
                "Somehow lose it."
            )),

            Pair("A test is coming up. How do you study for it?",
                arrayListOf(
                    "Study hard.",
                    "At the last second. ",
                    "Ignore it and play!",
                    "Umm, what test?"
                )),

            Pair("A human hand extends out of a toilet! What would you do?",
                arrayListOf(
                    "Scream and run. ",
                    "Close the lid without a word.",
                    "Shake hands with it.",
                    ""
                )),

            Pair("Are you often late for school or meetings?",
                arrayListOf(
                    "Yes",
                    "No",
                    "",
                    ""
                )),

            Pair("Can you go into a haunted house?",
                arrayListOf(
                    "No problem",
                    "Uh...n-no...",
                    "With someone I like.",
                    ""
                )),

            Pair("Do you fall asleep without noticing?",
                arrayListOf(
                    "Yes",
                    "No",
                    "",
                    ""
                )),

            Pair("Do you like puns?",
                arrayListOf(
                    "Love them!",
                    "A little",
                    "Spare me.. ",
                    ""
                )),

            Pair("Do you sometimes run out of things to do all of a sudden?",
                arrayListOf(
                    "Yes",
                    "No",
                    "",
                    ""
                )),

            Pair("It's the summer holidays! Where would you like to go?",
                arrayListOf(
                    "The beach! ",
                    "Spas.",
                    "The mountains.",
                    "Anywhere."
                )),

            Pair("There is an alien invasion! What will you do?",
                arrayListOf(
                    "Fight!",
                    "Run.",
                    "Ignore it.",
                    ""
                ))
        )


}