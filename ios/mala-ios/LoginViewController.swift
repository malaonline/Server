//
//  LoginViewController.swift
//  mala-ios
//
//  Created by Liang Sun on 11/10/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

class LoginViewController: UIViewController {
    
    @IBOutlet weak var inputPhoneNumber: UITextField!
    @IBOutlet weak var inputPassword: UITextField!
    
    var phoneNumber: String {
        get {
            return inputPhoneNumber.text!
        }
        set {
            inputPhoneNumber.text = newValue
        }
    }
    
    var password: String {
        get {
            return inputPassword.text!
        }
        set {
            inputPassword.text = newValue
        }
    }
    
    @IBAction func onLoginBtnClicked() {
        // TODO: Need perform http request with NSURLConnection
        // Fake login
        if phoneNumber == "15801690996" && password == "mala" {
            self.performSegueWithIdentifier("login", sender: self)
        }
    }
    
    @IBAction func onExitBtnClicked() {
        //dismissViewControllerAnimated(true, completion: {})
    }
    
    @IBAction func onBack(segue: UIStoryboardSegue) {
        // only clear the password field
        //phoneNumber.removeAll()
        password.removeAll()
    }
    
}