package com.example.uvlevel.data

// result generated from /json

data class Base(val result: Result?)

data class Result(val uv: Number?, val uv_time: String?, val uv_max: Number?, val uv_max_time: String?, val ozone: Number?, val ozone_time: String?, val safe_exposure_time: Safe_exposure_time?, val sun_info: Sun_info?)

data class Safe_exposure_time(val st1: Any?, val st2: Any?, val st3: Any?, val st4: Any?, val st5: Any?, val st6: Any?)

data class Sun_info(val sun_times: Sun_times?, val sun_position: Sun_position?)

data class Sun_position(val azimuth: Number?, val altitude: Number?)

data class Sun_times(val solarNoon: String?, val nadir: String?, val sunrise: String?, val sunset: String?, val sunriseEnd: String?, val sunsetStart: String?, val dawn: String?, val dusk: String?, val nauticalDawn: String?, val nauticalDusk: String?, val nightEnd: String?, val night: String?, val goldenHourEnd: String?, val goldenHour: String?)