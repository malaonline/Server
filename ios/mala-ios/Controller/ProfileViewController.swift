//
//  ProfileViewController.swift
//  mala-ios
//
//  Created by Elors on 12/18/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

class ProfileViewController: UITableViewController {

    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()

        println("Profile Did Load")
        println("UserToken is \(MalaUserDefaults.userAccessToken.value)")
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

    
    // MARK: - DataSource
    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 0
    }

    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 0
    }
}
