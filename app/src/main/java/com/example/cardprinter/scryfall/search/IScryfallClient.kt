package com.example.cardprinter.scryfall.search

import android.graphics.Bitmap
import com.example.cardprinter.scryfall.autocomplete.AutocompleteRequest
import com.example.cardprinter.scryfall.catalog.Catalog
import com.example.cardprinter.scryfall.named.NamedRequest

interface IScryfallClient {

    suspend fun AutocompleteCards(request: AutocompleteRequest): Catalog

    suspend fun NamedCards(request: NamedRequest): Bitmap
}