//
//  LoginViewController.swift
//  mala-ios
//
//  Created by Liang Sun on 11/10/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit
import SwiftyJSON

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
    let params = ["username": phoneNumber, "password": password]
    do {
      let opt = try HTTP.POST(BackAPI.tokenAuth, parameters: params)
      opt.start { response in
        // do things ...
        let json = JSON(data: response.data)

        if json["token"].string == nil {
          print("Expect 'token' in: ", BackAPI.tokenAuth)
          print(response.text)
          return
        }

        let token = json["token"].string
        print("Login OK! token: ", token)

        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0)) {
          // do some task
          dispatch_async(dispatch_get_main_queue()) {
            // update some UI
            self.performSegueWithIdentifier("login", sender: self)
          }
        }
      }
    } catch let error {
      print("got an error creating the request: \(error)")
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