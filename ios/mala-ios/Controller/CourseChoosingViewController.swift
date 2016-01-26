//
//  CourseChoosingViewController.swift
//  mala-ios
//
//  Created by 王新宇 on 1/22/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class CourseChoosingViewController: UIViewController {

    // MARK: - Property
    /// 教师详情数据模型
    var teacherModel: TeacherDetailModel? {
        didSet {
            self.tableView.teacherModel = teacherModel
        }
    }
    /// 上课地点数据模型
    var schoolArray: [SchoolModel] = [] {
        didSet {
            self.tableView.schoolModel = schoolArray
        }
    }
    /// 当前课程选择对象
    var choosingObject: CourseChoosingObject?
    /// 上课地点Cell打开标识
    var isOpenSchoolsCell: Bool = false
    /// 当前上课地点记录下标
    var selectedSchoolIndexPath: NSIndexPath  = NSIndexPath(forRow: 0, inSection: 0)
    
    
    // MARK: - Compontents
    private lazy var tableView: CourseChoosingTableView = {
        let tableView = CourseChoosingTableView(frame: CGRectZero, style: .Grouped)
        return tableView
    }()
    private lazy var confirmView: CourseChoosingConfirmView = {
        let confirmView = CourseChoosingConfirmView()
        return confirmView
    }()
    
    
    // MARK: - Contructed
    override func viewDidLoad() {
        super.viewDidLoad()
        
        setupUserInterface()
        loadSchoolsData()
        setupNotification()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    
    // MARK: - Private method
    private func setupUserInterface() {
        // Style
        makeStatusBarBlack()
        
        // 设置BarButtomItem间隔
        let spacer = UIBarButtonItem(barButtonSystemItem: .FixedSpace, target: nil, action: nil)
        spacer.width = -MalaLayout_Margin_5*2.3
        
        // leftBarButtonItem
        let leftBarButtonItem = UIBarButtonItem(customView:
            UIButton(
                imageName: "leftArrow_normal",
                highlightImageName: "leftArrow_press",
                target: self.navigationController,
                action: "popViewControllerAnimated:"
            )
        )
        navigationItem.leftBarButtonItems = [spacer, leftBarButtonItem]
        self.title = MalaCommonString_CourseChoosing
        
        // SubViews
        view.addSubview(confirmView)
        view.addSubview(tableView)
        
        // Autolayout
        confirmView.snp_makeConstraints { (make) -> Void in
            make.bottom.equalTo(self.view.snp_bottom)
            make.left.equalTo(self.view.snp_left)
            make.right.equalTo(self.view.snp_right)
            make.height.equalTo(47)
        }
        tableView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.view.snp_top)
            make.left.equalTo(self.view.snp_left)
            make.right.equalTo(self.view.snp_right)
            make.bottom.equalTo(confirmView.snp_top)
        }
    }
    
    private func loadSchoolsData() {
        // // 获取 [教学环境] 数据
        NetworkTool.sharedTools.loadSchools{[weak self] (result, error) -> () in
            if error != nil {
                debugPrint("TeacherDetailsController - loadSchools Request Error")
                return
            }
            guard let dict = result as? [String: AnyObject] else {
                debugPrint("TeacherDetailsController - loadSchools Format Error")
                return
            }
            
            // result 字典转 [SchoolModel] 模型数组
            let resultArray = ResultModel(dict: dict).results
            var tempArray: [SchoolModel] = []
            for object in resultArray ?? [] {
                if let dict = object as? [String: AnyObject] {
                    let set = SchoolModel(dict: dict)
                    tempArray.append(set)
                }
            }
            self?.schoolArray = tempArray 
        }
    }
    
    private func setupNotification() {
        // 授课年级选择
        NSNotificationCenter.defaultCenter().addObserverForName(
            MalaNotification_ChoosingGrade,
            object: nil,
            queue: nil) { [weak self] (notification) -> Void in
                // 保存用户所选课程
                let price = notification.object as! GradePriceModel
                self?.choosingObject?.price = price
        }
        // 选择上课地点
        NSNotificationCenter.defaultCenter().addObserverForName(
            MalaNotification_ChoosingSchool,
            object: nil,
            queue: nil
            ) { [weak self] (notification) -> Void in
                let school = notification.object as! schoolChoosingObject

                self?.tableView.isOpenSchoolsCell = school.isOpenCell
                
                if school.isOpenCell {
                    // 设置所有上课地点数据，设置选中样式
                    self?.tableView.selectedIndexPath = self?.selectedSchoolIndexPath
                    self?.tableView.schoolModel = self?.schoolArray ?? []
                }else if school.schoolModel != nil {
                    // 保存用户所选上课地点
                    self?.choosingObject?.school = school.schoolModel
                    // 设置tableView 的数据源和选中项
                    self?.tableView.schoolModel = [school.schoolModel!]
                    self?.selectedSchoolIndexPath = school.selectedIndexPath!
                }
        }
    }
    
    deinit {
        print("choosing Controller deinit")
        NSNotificationCenter.defaultCenter().removeObserver(self, name: MalaNotification_ChoosingGrade, object: nil)
        NSNotificationCenter.defaultCenter().removeObserver(self, name: MalaNotification_OpenSchoolsCell, object: nil)
    }
}




class CourseChoosingObject: NSObject {
    
    var price: GradePriceModel?
    var school: SchoolModel?
    
}