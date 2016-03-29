//
//  MalaSingleWebViewController.swift
//  mala-ios
//
//  Created by 王新宇 on 3/29/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit
import WebKit

class MalaSingleWebViewController: UIViewController, WKNavigationDelegate, WKUIDelegate {

    // MARK: - Property
    /// HTML代码请求路径。注意此属性并非webView地址
    var url: String = "" {
        didSet {
            loadHTML()
        }
    }
    /// 当前加载的HTML代码
    var HTMLString: String = "" {
        didSet {
            
            // 当HTML数据变化时，加载webView
            if HTMLString != oldValue {
                showHTML()
            }
        }
    }
    
    
    // MARK: - Components
    /// 网页视图
    private lazy var webView: WKWebView = {
        let configuration = WKWebViewConfiguration()
        configuration.preferences = WKPreferences()
        configuration.preferences.minimumFontSize = MalaLayout_FontSize_13
        
        let webView = WKWebView(frame: self.view.bounds, configuration: configuration)
        return webView
    }()
    
    
    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        
        configure()
        setupUserInterface()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    
    // MARK: - Private Method
    private func configure() {
        webView.navigationDelegate = self
        webView.UIDelegate = self
        
        // webView.addObserver(self, forKeyPath: "loading", options: .New, context: nil)
        // webView.addObserver(self, forKeyPath: "title", options: .New, context: nil)
        // webView.addObserver(self, forKeyPath: "estimatedProgress", options: .New, context: nil)
    }
    
    private func setupUserInterface() {
        // SubViews
        view.addSubview(webView)
        
        // Autolayout
        webView.snp_makeConstraints { (make) -> Void in
            make.center.equalTo(self.view.snp_center)
            make.size.equalTo(self.view.snp_size)
        }
    }
    
    private func loadHTML() {
        
        ///  获取用户协议HTML
        getUserProtocolHTML({ (reason, errorMessage) -> Void in
            defaultFailureHandler(reason, errorMessage: errorMessage)
            
            // 错误处理
            if let errorMessage = errorMessage {
                println("MalaSingleWebViewController - loadHTML Error \(errorMessage)")
            }
        }, completion: { [weak self] (string) -> Void in
            
            dispatch_async(dispatch_get_main_queue(), { () -> Void in
                if let htmlString = string {
                    self?.HTMLString = htmlString
                }else {
                    self?.ShowTost("网络不稳定，请重试")
                }
            })
        })
    }
    
    private func showHTML() {
        webView.loadHTMLString(HTMLString, baseURL: MalaBaseURL)
    }
    
    
    // MARK: - KVO
    override func observeValueForKeyPath(keyPath: String?, ofObject object: AnyObject?, change: [String : AnyObject]?, context: UnsafeMutablePointer<Void>) {
        
        guard keyPath != nil else {
            return
        }
        
        switch keyPath! {
        case "title":
            
            self.title = webView.title
            
            break
        case "loading":
            
            println("loading")
            
            break
        case "estimatedProgress":
            
            println("estimatedProgress - \(webView.estimatedProgress)")
            
            break
        default:
            break
        }
    }
}