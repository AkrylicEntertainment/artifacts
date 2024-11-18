package dev.nateweisz.bytestore.user.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView

@RestController
class UserController {

    @RequestMapping("/", method = [RequestMethod.GET])
    fun getHomePage(): ModelAndView = ModelAndView("home")
}