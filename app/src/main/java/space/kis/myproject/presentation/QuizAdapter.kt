package space.kis.myproject.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import space.kis.myproject.R
import space.kis.myproject.domain.CountryModel

class QuizAdapter : RecyclerView.Adapter<QuizAdapter.CountryViewHolder>() {
    class CountryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    var countries: List<CountryModel>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    private val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<CountryModel>() {
        override fun areItemsTheSame(oldItem: CountryModel, newItem: CountryModel) = false
        override fun areContentsTheSame(oldItem: CountryModel, newItem: CountryModel) = false
    })

    private var onItemClickListener: ((CountryModel) -> Unit)? = null

    fun setOnItemClickListener(listener: (CountryModel) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return countries.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        return CountryViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.quiz_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        val country = countries[position]

        (holder.itemView as AppCompatButton).apply {
            background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.answer_background)
            text = country.name
        }

        holder.itemView.setOnClickListener {
            it.background = if (country.correct == true)
                ContextCompat.getDrawable(it.context, R.drawable.correct_answer_background)
            else
                ContextCompat.getDrawable(it.context, R.drawable.incorrect_answer_background)
            onItemClickListener?.let { click ->
                click(country)
            }
        }
    }
}