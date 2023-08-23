package de.kassel.cc22023.roadtrip.data.network.model

data class UnsplashResponse(
    val results: List<UnsplashResult>
)

data class UnsplashResult(
    val urls: UnsplashUrls
)

data class UnsplashUrls(
    val regular: String
)