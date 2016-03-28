//
//  Mala+UIVIewController.swift
//  mala-ios
//
//  Created by 王新宇 on 3/28/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import Foundation

extension UIViewController {
    
    public func ShowTost(message: String) {
        self.navigationController?.view.makeToast(message)
    }
}
