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
        
        if let naviView = self.navigationController?.view {
            naviView.makeToast(message)
        }else if let view = self.view {
            view.makeToast(message)
        }
    }
    
    public func showActivity() {
        
        if let naviView = self.navigationController?.view {
            naviView.makeToastActivity(.Center)
        }else if let view = self.view {
            view.makeToastActivity(.Center)
        }
    }
    
    public func hideActivity() {
        
        if let naviView = self.navigationController?.view {
            naviView.hideToastActivity()
        }else if let view = self.view {
            view.hideToastActivity()
        }
    }
    
}
