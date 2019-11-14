package uk.cg0.okuru.config

data class Config(
    var url:String,
    var headers:HashMap<String,String>,
    var url_paramaters:HashMap<String,String>,
    var body_name_value:HashMap<String,String>,
    var request_fileformname:String,
    var method:String,
    var response_url_format:String
) {
    override fun toString(): String {
        return url
    }
}