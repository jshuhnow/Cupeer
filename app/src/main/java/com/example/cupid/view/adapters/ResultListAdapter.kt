package com.example.cupid.view.adapters

import android.app.Activity
import android.content.Context
import com.example.cupid.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cupid.view.data.ResultUI
import com.example.cupid.view.utils.getAvatarFromId
import kotlinx.android.synthetic.main.item_question_result.view.*



class ResultListAdapter(
    private var results: List<ResultUI> = emptyList(),
    private var context : Activity? = null
) : RecyclerView.Adapter<ResultListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            inflater.inflate(
                R.layout.item_question_result,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = results[position]

        holder.question.text = result.question_text
        holder.answerYou.text  = result.answerYou
        holder.answerPartner.text  = result.answerPartner
        holder.nameYou.text  = result.nameYou
        holder.namePartner.text = result.namePartner
        holder.iconYou.setImageResource(getAvatarFromId(context as Context,result.iconIdYou))
        holder.iconPartner.setImageResource(getAvatarFromId(context as Context,result.iconIdPartner))

        //val bgColors = listOf(R.drawable.question_result_bg1, R.color.gradientMiddleActive, R.color.gradientStartActive)
        val bgColors = listOf(R.color.gradientEndActive, R.color.gradientMiddleActive, R.color.gradientStartActive)
        holder.itemView.layout_result_question.background = context!!.getDrawable(bgColors[position])
    }

    override fun getItemCount(): Int {
        return results.size
    }

    fun setResults(results: List<ResultUI>) {
        this.results = results
    }

    fun getResults(): List<ResultUI> {
        return results
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val question: TextView = view.text_result_question
        val answerYou : TextView = view.text_result_youranswer
        val answerPartner : TextView = view.text_result_partneranswer
        val nameYou : TextView = view.text_result_you
        val namePartner : TextView = view.text_result_partner
        val iconYou : ImageView = view.image_result_you
        val iconPartner : ImageView = view.image_result_partner
    }

}