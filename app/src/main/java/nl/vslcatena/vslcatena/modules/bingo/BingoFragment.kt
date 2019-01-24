package nl.vslcatena.vslcatena.modules.bingo


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_bingo.*
import nl.vslcatena.vslcatena.R
import nl.vslcatena.vslcatena.BaseFragment
import nl.vslcatena.vslcatena.models.Identifier
import nl.vslcatena.vslcatena.util.data.observeList
import nl.vslcatena.vslcatena.util.login.NeedsAuthentication

/**
 * A simple [Fragment] subclass.
 *
 */

@NeedsAuthentication
class BingoFragment : BaseFragment() {
    private lateinit var bingoViewModel: BingoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bingoViewModel = ViewModelProviders.of(this).get(BingoViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.fragment_bingo, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        FirebaseFirestore.getInstance().collection("bingo").get().addOnCompleteListener { task ->
            task.result?.let { snapshot ->
                val bingoItems = snapshot.map { it.getString("text")!! }
                bingoViewModel.initialize(bingoItems, boardSize)
                bingoGrid.apply {
                    numColumns = boardSize
                    adapter = BingoAdapter()
                }
            }
        }

        bingoViewModel.won.observe(this, Observer {
            if (!it) return@Observer
            Toast.makeText(context!!, "Has won!", Toast.LENGTH_LONG).show()
        })
    }

    inner class BingoAdapter : BaseAdapter() {
        override fun getCount(): Int = boardSize * boardSize

        override fun getItemId(position: Int) = 0L

        override fun getItem(position: Int) = null

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val bingoCell =
                bingoViewModel.grid[position / boardSize][position % boardSize]

            val bingoView =
                if (convertView == null)
                    LayoutInflater.from(context!!).inflate(
                        R.layout.fragment_bingo_field,
                        parent,
                        false
                    )
                else convertView

            bingoView.setOnClickListener {
                bingoCell.toggle()
            }

            bingoView.findViewById<TextView>(R.id.bingo_item_title)
                .text = bingoCell.text

            bingoCell.isChecked.observe(
                this@BingoFragment,
                Observer {
                    bingoView.setBackgroundResource(
                        if (it == true) R.drawable.bingo_field_border_checked
                        else R.drawable.bingo_field_border
                    )
                }
            )

            return bingoView
        }
    }

    companion object {
        const val boardSize = 5
    }
}
