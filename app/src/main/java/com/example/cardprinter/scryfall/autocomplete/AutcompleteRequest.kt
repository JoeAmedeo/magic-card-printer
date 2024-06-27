package com.example.cardprinter.scryfall.autocomplete

class AutocompleteRequest(val q: String, val format: String = "json", val pretty: Boolean = false, val includeExtras: Boolean = true)