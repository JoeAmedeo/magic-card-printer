package com.example.cardprinter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.cardprinter.scryfall.autocomplete.AutocompleteRequest
import com.example.cardprinter.scryfall.named.NamedRequest
import com.example.cardprinter.scryfall.search.IScryfallClient
import com.example.cardprinter.scryfall.search.ScryfallClient
import com.example.cardprinter.ui.theme.CardPrinterTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // on below line we are
    // creating variables for listview
    lateinit var catalogListView: ListView

    // creating variable for searchview
    lateinit var searchView: SearchView

    // using this to store catalog info
    lateinit var currentSearchCatalog: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initializing variables of list view with their ids.
        catalogListView = findViewById(R.id.idLVProgrammingLanguages)
        searchView = findViewById(R.id.idSV)

        currentSearchCatalog = ArrayList<String>()

        val listAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            currentSearchCatalog
        )

        catalogListView.adapter = listAdapter

        // get dependency injections from app container
        val scryfallClient: IScryfallClient = ScryfallClient()

        val context: Context = this

        catalogListView.setOnItemClickListener { parent, view, position, id ->
            val cardName = listAdapter.getItem(position)
            EnterDemoActivity(context, cardName!!)
        }

        // on below line we are adding on query
        // listener for our search view.
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(cardName: String?): Boolean {
                EnterDemoActivity(context, cardName!!)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if (newText.isNullOrBlank()) {
                    return true
                }

                try {
                    val autocompleteRequest = AutocompleteRequest(q = newText)
                    CoroutineScope(Dispatchers.Default).launch {
                        currentSearchCatalog = scryfallClient.AutocompleteCards(autocompleteRequest).data
                        runOnUiThread {
                            listAdapter.clear()
                            listAdapter.addAll(currentSearchCatalog)
                            listAdapter.notifyDataSetChanged()
                        }
                    }
                    return true
                } catch (e: Exception) {

                    return true
                }

                return false
            }
        })
    }

    fun EnterDemoActivity(context: Context, cardName: String) {
        val intent = Intent(context, DemoActivity::class.java)
        intent.putExtra("name", cardName)
        startActivity(intent)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CardPrinterTheme {
        Greeting("Android")
    }
}