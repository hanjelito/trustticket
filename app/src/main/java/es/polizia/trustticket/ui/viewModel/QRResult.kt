package es.polizia.trustticket.ui.viewModel

sealed class QRResult {
    object Loading : QRResult()
    data class Success(val qrJwt: String) : QRResult()
    data class LocationError(val message: String) : QRResult()
    data class Error(val message: String) : QRResult()
}