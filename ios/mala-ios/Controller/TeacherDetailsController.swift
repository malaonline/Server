//
//  TeacherDetailsController.swift
//  mala-ios
//
//  Created by Elors on 12/30/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

private let TeacherDetailCellReusedId = "TeacherDetailCellReusedId"

class TeacherDetailsController: UITableViewController {

    // MARK: - Variables
    var model: TeacherDetailModel?
    
    
    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        
        print("Data: ==== \(self.model)")
        tableView.registerClass(TeacherDetailsBaseCell.self, forCellReuseIdentifier: TeacherDetailCellReusedId)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    // MARK: - DataSource
    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 9
    }
    
    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 1
    }
    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier(TeacherDetailCellReusedId, forIndexPath: indexPath) as! TeacherDetailsBaseCell
        cell.title = "风格标签好"
        return cell
    }

    
    // MARK: - Deleagte
    override func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        return 300.0
    }
}
