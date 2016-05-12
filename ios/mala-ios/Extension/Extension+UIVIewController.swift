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
    
    
    // MARK: - Badge Point
    var showTabBadgePoint : Bool {
        get {
            return !tabBadgePointView.hidden
        }
        set {
            if newValue && tabBadgePointView.superview == nil {
                tabBadgePointView.center = tabBadgePointViewCenter
                if let tbb = tabBarButton {
                    tbb.addSubview(tabBadgePointView)
                }
            }
            tabBadgePointView.hidden = newValue == false
        }
    }
    
    var tabBadgePointView : UIView {
        
        get {
            var _tabBadgePointView = objc_getAssociatedObject(self, &AssociatedKeys.tabBadgePointViewAssociatedKey)
            if _tabBadgePointView == nil {
                _tabBadgePointView = defaultTabBadgePointView()
                objc_setAssociatedObject(self, &AssociatedKeys.tabBadgePointViewAssociatedKey, _tabBadgePointView, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
            }
            return _tabBadgePointView as! UIView
        }
        set {
            if newValue.superview != nil {
                newValue.removeFromSuperview()
            }
            newValue.hidden = true
            objc_setAssociatedObject(self, &AssociatedKeys.tabBadgePointViewAssociatedKey, newValue, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
        }
    }
    
    var tabBadgePointViewOffset : UIOffset {
        
        get {
            if let obj = objc_getAssociatedObject(self, &AssociatedKeys.tabBadgePointViewOffsetAssociatedKey) {
                return obj.UIOffsetValue()
            }
            else {
                return UIOffsetZero
            }
        }
        set {
            objc_setAssociatedObject(self, &AssociatedKeys.tabBadgePointViewOffsetAssociatedKey, NSValue(UIOffset: newValue), .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
        }
    }
    
    var isEmbedInTabBarController : Bool {
        var _isEmbedInTabBarController = false
        if let tbc = self.tabBarController, vcs = tbc.viewControllers {
            for i in 0 ..< vcs.count {
                let vc = vcs[i]
                if vc == self {
                    _isEmbedInTabBarController = true
                    tabIndex = i
                    break
                }
            }
        }
        return _isEmbedInTabBarController
    }
    
    var tabIndex : Int {
        
        get {
            if isEmbedInTabBarController == false {
                print("LxTabBadgePoint：This viewController not embed in tabBarController")
                return NSNotFound
            }
            let obj = objc_getAssociatedObject(self, &AssociatedKeys.tabIndexAssociatedKey)
            return obj.integerValue
        }
        set {
            objc_setAssociatedObject(self, &AssociatedKeys.tabIndexAssociatedKey, newValue, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
        }
    }
    
    var tabBarButton : UIView? {
        get {
            if let tbc = tabBarController {
                var tabBarButtonArray = [UIView]()
                for subView in tbc.tabBar.subviews {
                    if let className = NSString(CString: object_getClassName(subView), encoding: NSUTF8StringEncoding) {
                        if className.hasPrefix("UITabBarButton") {
                            tabBarButtonArray.append(subView)
                        }
                    }
                }
                
                tabBarButtonArray.sortInPlace({ (subView1, subView2) -> Bool in
                    return subView1.frame.minX < subView2.frame.minX
                })
                
                if tabIndex >= 0 && tabIndex < tabBarButtonArray.count {
                    return tabBarButtonArray[tabIndex]
                }else {
                    print("Extension: TabBadgePoint：Not found corresponding tabBarButton!")
                    return nil
                }
            }
            else {
                
                return nil
            }
        }
    }
    
    // MARK: BadgePoint Private Method
    
    private var tabBadgePointViewCenter : CGPoint {
        
        get {
            if let tbb = tabBarButton {
                
                var tabBadgePointViewCenter = CGPoint(x: tbb.bounds.midX + 14, y : 8.5)
                tabBadgePointViewCenter.x += tabBadgePointViewOffset.horizontal
                tabBadgePointViewCenter.y += tabBadgePointViewOffset.vertical
                return tabBadgePointViewCenter
            }
            else {
                return CGPoint(x: 36, y: 8.5)
            }
        }
    }
    
    private func defaultTabBadgePointView() -> UIView {
        
        let defaultTabBadgePointViewRadius = 4.5
        
        let defaultTabBadgePointViewFrame = CGRect(origin: CGPointZero, size: CGSize(width: defaultTabBadgePointViewRadius * 2, height: defaultTabBadgePointViewRadius * 2))
        
        let defaultTabBadgePointView = UIView(frame: defaultTabBadgePointViewFrame)
        defaultTabBadgePointView.backgroundColor = UIColor.redColor()
        defaultTabBadgePointView.layer.cornerRadius = CGFloat(defaultTabBadgePointViewRadius)
        defaultTabBadgePointView.layer.masksToBounds = true
        
        defaultTabBadgePointView.hidden = true
        
        return defaultTabBadgePointView
    }
    
    private struct AssociatedKeys {
        static var tabIndexAssociatedKey = "tabIndexAssociatedKey"
        static var tabBadgePointViewAssociatedKey = "tabBadgePointViewAssociatedKey"
        static var tabBadgePointViewOffsetAssociatedKey = "tabBadgePointViewOffsetAssociatedKey"
    }
}