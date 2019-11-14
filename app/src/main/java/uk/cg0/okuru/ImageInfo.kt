package uk.cg0.okuru

data class ImageInfo(var imageUrl: String, var imageName: String) {
    override fun toString(): String {
        return imageUrl
    }
}


