package com.example.churrasquinhoapp.utils

object Constants {
    // Database
    const val DATABASE_NAME = "churrasquinho_db"
    const val DATABASE_VERSION = 1

    // Tables
    const val TABLE_ITEMS = "items"
    const val TABLE_SALES = "sales"

    // Shared Preferences
    const val PREFS_NAME = "churrasquinho_prefs"
    const val PREF_FIRST_RUN = "first_run"
    const val PREF_LAST_SYNC = "last_sync"

    // Intent Keys
    const val EXTRA_ITEM_ID = "item_id"
    const val EXTRA_SALE_ID = "sale_id"
    const val EXTRA_SELECTED_DATE = "selected_date"

    // Request Codes
    const val REQUEST_ADD_ITEM = 100
    const val REQUEST_EDIT_ITEM = 101
    const val REQUEST_ADD_SALE = 102

    // Time Constants
    const val MILLIS_PER_DAY = 24 * 60 * 60 * 1000L
    const val MILLIS_PER_HOUR = 60 * 60 * 1000L
    const val MILLIS_PER_MINUTE = 60 * 1000L

    // Default Values
    const val DEFAULT_MINIMUM_STOCK = 5
    const val DEFAULT_QUANTITY = 1
    const val DEFAULT_COST_PRICE = 0.0
    const val DEFAULT_SELLING_PRICE = 0.0

    // Validation Constants
    const val MIN_NAME_LENGTH = 3
    const val MAX_NAME_LENGTH = 50
    const val MIN_QUANTITY = 0
    const val MIN_PRICE = 0.0
    const val MIN_STOCK = 0

    // UI Constants
    const val ANIMATION_DURATION_SHORT = 150L
    const val ANIMATION_DURATION_MEDIUM = 300L
    const val ANIMATION_DURATION_LONG = 500L

    const val CLICK_DEBOUNCE_TIME = 300L
    const val SEARCH_DEBOUNCE_TIME = 500L

    // Error Messages
    object ErrorMessages {
        const val GENERIC_ERROR = "Ocorreu um erro. Tente novamente."
        const val NETWORK_ERROR = "Erro de conexão. Verifique sua internet."
        const val DATABASE_ERROR = "Erro ao acessar o banco de dados."
        const val INVALID_INPUT = "Dados inválidos. Verifique os campos."
        const val INSUFFICIENT_STOCK = "Estoque insuficiente."
        const val ITEM_NOT_FOUND = "Item não encontrado."
        const val SALE_NOT_FOUND = "Venda não encontrada."
    }

    // Success Messages
    object SuccessMessages {
        const val ITEM_SAVED = "Item salvo com sucesso."
        const val ITEM_DELETED = "Item excluído com sucesso."
        const val SALE_REGISTERED = "Venda registrada com sucesso."
        const val STOCK_UPDATED = "Estoque atualizado com sucesso."
    }

    // Date Formats
    object DateFormats {
        const val DATE_FORMAT = "dd/MM/yyyy"
        const val TIME_FORMAT = "HH:mm"
        const val DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm"
        const val DATE_TIME_FORMAT_WITH_SECONDS = "dd/MM/yyyy HH:mm:ss"
        const val API_DATE_FORMAT = "yyyy-MM-dd"
        const val API_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
    }

    // Currency
    object Currency {
        const val LOCALE_LANGUAGE = "pt"
        const val LOCALE_COUNTRY = "BR"
        const val CURRENCY_SYMBOL = "R$"
    }

    // Units
    object Units {
        const val KILOGRAM = "kg"
        const val UNIT = "un"
        const val GRAM = "g"
        const val LITER = "L"
        const val MILLILITER = "ml"
    }

    // Dialog Actions
    object DialogActions {
        const val CONFIRM = "Confirmar"
        const val CANCEL = "Cancelar"
        const val OK = "OK"
        const val YES = "Sim"
        const val NO = "Não"
        const val SAVE = "Salvar"
        const val DELETE = "Excluir"
    }
}