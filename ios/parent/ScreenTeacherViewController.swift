//
//  ScreenTeacherViewController.swift
//  mala-ios
//
//  Created by Liang Sun on 11/10/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit
import SwiftyJSON

class ScreenTeacherViewController: UIViewController {

  @IBOutlet weak var pickerView: UIPickerView!

  var grades = JSON([])
  var subjects = JSON([])

  func numberOfComponentsInPickerView(pickerView: UIPickerView) -> Int {
    return 2
  }

  func pickerView(pickerView: UIPickerView, numberOfRowsInComponent component: Int)->Int {
    switch component {
    case 0:
      return grades.count
    case 1:
      return subjects.count
    default:
      return 0
    }
  }

  func pickerView(pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String! {
    switch component {
    case 0:
      return grades[row]["name"].string
    case 1:
      return subjects[row]["name"].string
    default:
      return "error"
    }
  }

  func pickerView(pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
    switch component {
    case 0:
      // reload sub level
      subjects = grades[row]["subjects"]
      pickerView.reloadComponent(1)
    default:
      return
    }
  }

  @IBAction func onBtnClicked() {
    // todo: go to teacher list view page
  }

  override func viewDidLoad() {
    super.viewDidLoad()
    // Do any additional setup after loading the view, typically from a nib.
    do {
      let opt = try HTTP.GET(BackAPI.grades)
      opt.start { response in
        // do things ...
        let json = JSON(data: response.data)

        if json["results"].array == nil {
          print("Expect 'results' in: ", BackAPI.grades)
          print(response.text)
          return
        }
        self.grades = json["results"]

        if self.grades[0]["subjects"].array == nil {
          print("Expect 'subjects' in: ", BackAPI.grades)
          print(response.text)
          return
        }
        self.subjects = self.grades[0]["subjects"]

        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0)) {
          // do some task
          dispatch_async(dispatch_get_main_queue()) {
            // update some UI
            self.pickerView.reloadAllComponents()
          }
        }
      }
    } catch let error {
      print("got an error creating the request: \(error)")
    }
  }
}