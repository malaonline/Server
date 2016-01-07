//
//  TeacherDetailsController.swift
//  mala-ios
//
//  Created by Elors on 12/30/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

private let TeacherDetailsCellReuseId = [
    0: "TeacherDetailsSubjectCellReuseId",
    1: "TeacherDetailsTagsCellReuseId",
    2: "TeacherDetailsHighScoreCellReuseId",
    3: "TeacherDetailsPhotosCellReuseId",
    4: "TeacherDetailsCertificateCellReuseId",
    5: "TeacherDetailsPlaceCellReuseId",
    6: "TeacherDetailsVipServiceCellReuseId",
    7: "TeacherDetailsLevelCellReuseId",
    8: "TeacherDetailsPriceCellReuseId"
]

class TeacherDetailsController: UITableViewController, UIGestureRecognizerDelegate {

    // MARK: - Variables
    var model: TeacherDetailModel?
    
    private lazy var headerBackground: UIImageView = {
        let image = UIImageView(image: UIImage(named: "headerBackground"))
        image.contentMode = .ScaleAspectFill
        return image
    }()
    
    
    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()

        tableView.registerClass(TeacherDetailsSubjectCell.self, forCellReuseIdentifier: TeacherDetailsCellReuseId[0]!)
        tableView.registerClass(TeacherDetailsTagsCell.self, forCellReuseIdentifier: TeacherDetailsCellReuseId[1]!)
        tableView.registerClass(TeacherDetailsHighScoreCell.self, forCellReuseIdentifier: TeacherDetailsCellReuseId[2]!)
        tableView.registerClass(TeacherDetailsPhotosCell.self, forCellReuseIdentifier: TeacherDetailsCellReuseId[3]!)
        tableView.registerClass(TeacherDetailsCertificateCell.self, forCellReuseIdentifier: TeacherDetailsCellReuseId[4]!)
        tableView.registerClass(TeacherDetailsPlaceCell.self, forCellReuseIdentifier: TeacherDetailsCellReuseId[5]!)
        tableView.registerClass(TeacherDetailsVipServiceCell.self, forCellReuseIdentifier: TeacherDetailsCellReuseId[6]!)
        tableView.registerClass(TeacherDetailsLevelCell.self, forCellReuseIdentifier: TeacherDetailsCellReuseId[7]!)
        tableView.registerClass(TeacherDetailsPriceCell.self, forCellReuseIdentifier: TeacherDetailsCellReuseId[8]!)
    }
    
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        
        setupConfig()
        setupTableHeaderView()
        
        // setup style
        tableView.backgroundColor = UIColor(rgbHexValue: 0xededed, alpha: 1.0)
        tableView.separatorColor = UIColor(rgbHexValue: 0xdbdbdb, alpha: 1.0)
        
        // make clear color
        navigationController?.navigationBar.setBackgroundImage(UIImage(), forBarMetrics: .Default)
        navigationController?.navigationBar.shadowImage = UIImage()
        
        // setup headerImage
        tableView.insertSubview(headerBackground, atIndex: 0)
        headerBackground.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(0).offset(-64)
            make.left.equalTo(0)
            make.width.equalTo(MalaScreenWidth)
            make.height.equalTo(200)
        }
        
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
        tableView.estimatedRowHeight = 240
        self.navigationItem.leftBarButtonItem = UIBarButtonItem(image: UIImage(named: "leftArrow"), style: .Done, target: self.navigationController, action: "popViewControllerAnimated:")
        
        // Active Pop GestureRecognizer
        self.navigationController?.interactivePopGestureRecognizer?.delegate = self
        self.navigationController?.interactivePopGestureRecognizer?.enabled = true
    }
    
    private func setupTableHeaderView() {
        
        let headerView = TeacherDetailsHeaderView(frame: CGRect(x: 0, y: 0, width: MalaScreenWidth, height: MalaLayout_DetailHeaderHeight))
        headerView.avatar = (model?.avatar) ?? ""
        headerView.name = (model?.name) ?? "---"
        headerView.gender = (model?.gender) ?? "m"
        headerView.teachingAge = (model?.teaching_age) ?? 0
        
        tableView.tableHeaderView = headerView
    }
    
    
    // MARK: - DataSource
    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 9
    }
    
    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 1
    }
    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        
        let reuseCell = tableView.dequeueReusableCellWithIdentifier(TeacherDetailsCellReuseId[indexPath.section]!, forIndexPath: indexPath)
        (reuseCell as! TeacherDetailsBaseCell).title.text = MalaTeacherDetailsCellTitle[indexPath.section+1]
        
        switch indexPath.section {
        case 0:
            
            let cell = reuseCell as! TeacherDetailsSubjectCell
            var set: [String] = []
            for string in self.model!.grades {
                set.append(string + (self.model!.subject ?? ""))
            }
            cell.labels = []//set
            return cell
            
        case 1:
            let cell = reuseCell as! TeacherDetailsTagsCell
            cell.labels = []//self.model?.tags
            return cell
            
        case 2:
            let cell = reuseCell as! TeacherDetailsHighScoreCell
            return cell
            
        case 3:
            let cell = reuseCell as! TeacherDetailsPhotosCell
            cell.photos = self.model?.photo_set ?? []
            cell.accessory = .RightArrow
            return cell
            
        case 4:
            let cell = reuseCell as! TeacherDetailsCertificateCell
            cell.labels = []//self.model?.certificate_set
            return cell
            
        case 5:
            let cell = reuseCell as! TeacherDetailsPlaceCell
            return cell
            
        case 6:
            let cell = reuseCell as! TeacherDetailsVipServiceCell
            return cell
            
        case 7:
            let cell = reuseCell as! TeacherDetailsLevelCell
            cell.labels = []//[(self.model?.level)!]
            return cell
            
        case 8:
            let cell = reuseCell as! TeacherDetailsPriceCell
            return cell
            
        default:
            break
        }

        return reuseCell
    }
    
    override func tableView(tableView: UITableView, shouldHighlightRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        return false
    }

    
    // MARK: - Deleagte
    override func tableView(tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return section == 0 ? 8.0 : 4.0
    }
    
    override func tableView(tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return 4.0
    }
    
    
}
