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
    let params = ["username": phoneNumber, "password": password]
    do {
      let opt = try HTTP.POST(BackAPI.tokenAuth, parameters: params)
      opt.start { response in
        //do things...
        let data = response.data as NSData
        let str = NSString(data: data, encoding: NSUTF8StringEncoding)
        print("response: \(str)")

        let json = JSON(data: data)
        if let token = json["token"].string {
          dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0)) {
            // do some task
            print(token)
            dispatch_async(dispatch_get_main_queue()) {
              // update some UI
              self.performSegueWithIdentifier("login", sender: self)
            }
          }
        } else {
          print("JSON parse error: ")
          json["token"].string
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