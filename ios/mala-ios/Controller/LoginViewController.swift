//
//  LoginViewController.swift
//  mala-ios
//
//  Created by Erdi on 12/31/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

class LoginViewController: UIViewController {
    
    // MARK: - Variables

    // MARK: - Components
    private lazy var phoneNumberInput: UITextField = UITextField()
    private lazy var checkInput: UITextField = UITextField()
    private lazy var verifyButton: UIButton = UIButton()


    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        setupUI()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    private func setupUI() {

        phoneNumberInput.backgroundColor = UIColor.grayColor()
        view.addSubview(phoneNumberInput)

        phoneNumberInput.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(self.view.snp_left)
            make.right.equalTo(self.view.snp_right)
        }

    }

}