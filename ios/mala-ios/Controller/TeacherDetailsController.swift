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
        
        setupConfig()
        
        tableView.registerClass(TeacherDetailsBaseCell.self, forCellReuseIdentifier: TeacherDetailCellReusedId)
    }
    
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        
        // setup style
        tableView.backgroundColor = UIColor(rgbHexValue: 0xededed, alpha: 1.0)
        tableView.separatorColor = UIColor(rgbHexValue: 0xdbdbdb, alpha: 1.0)
        
        // make clear color
        navigationController?.navigationBar.setBackgroundImage(UIImage(), forBarMetrics: .Default)
        navigationController?.navigationBar.shadowImage = UIImage()
    }
    
    override func viewWillDisappear(animated: Bool) {
        super.viewWillDisappear(animated)
        navigationController?.navigationBar.setBackgroundImage(UIImage.withColor(UIColor.redColor()), forBarMetrics: .Default)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    // MARK: - Private Method
    private func setupConfig() {
        tableView.estimatedRowHeight = 120
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
        cell.title.text = MalaTeacherDetailsCellTitle[indexPath.section+1]
        cell.labels = self.model?.tags
        return cell
    }

    
    // MARK: - Deleagte
    override func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        return 100.0
    }
    
    override func tableView(tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 5.0
    }
    
    override func tableView(tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return 5.0
    }
    
    
}
