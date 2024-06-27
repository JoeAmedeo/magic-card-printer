package com.example.cardprinter.scryfall.search

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.cardprinter.scryfall.autocomplete.AutocompleteRequest
import com.example.cardprinter.scryfall.catalog.Catalog
import com.example.cardprinter.scryfall.named.NamedRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.get
import io.ktor.serialization.gson.*
import io.ktor.client.statement.readBytes

class ScryfallClient : IScryfallClient {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            gson()
        }
    }
    private val baseURL: String = "https://api.scryfall.com"
    private val autocompletePath = "/cards/autocomplete"
    private val namedPath = "/cards/named"

    override suspend fun AutocompleteCards(request: AutocompleteRequest): Catalog {

        val url = "${baseURL}${autocompletePath}?q=${request.q}&include_extras=true"
        val catalog: Catalog = client.get(urlString = url).body()
        return catalog
    }

    override suspend fun NamedCards(request: NamedRequest): Bitmap {
        val url = "${baseURL}${namedPath}?exact=${request.exact}&format=image&version=large"
        val imageBytes: ByteArray = client.get(url).readBytes()
        val imageBitmap: Bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        return imageBitmap
    }
}