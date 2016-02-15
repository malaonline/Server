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
    /// 课程表数据模型
    var classScheduleModel: [[ClassScheduleDayModel]] = [] {
        didSet {
            self.tableView.classScheduleModel = classScheduleModel
        }
    }
    /// 当前课程选择对象
    var choosingObject: CourseChoosingObject? = CourseChoosingObject()
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
        loadClassSchedule()
        setupNotification()
    }
    
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        makeStatusBarBlack()
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
                target: self,
                action: "popSelf"
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
                debugPrint("CourseChoosingViewController - loadSchools Request Error")
                return
            }
            guard let dict = result as? [String: AnyObject] else {
                debugPrint("CourseChoosingViewController - loadSchools Format Error")
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
    
    private func loadClassSchedule() {
        NetworkTool.sharedTools.loadClassSchedule((teacherModel?.id ?? 1), schoolId: (choosingObject?.school?.id ?? 1)) {
            [weak self] (result, error) -> () in
            if error != nil {
                debugPrint("CourseChoosingViewController - loadClassSchedule Request Error")
                return
            }
            guard let dict = result as? [String: AnyObject] else {
                debugPrint("CourseChoosingViewController - loadClassSchedule Format Error")
                return
            }
            
            // result字典转模型
            var modelArray: [[ClassScheduleDayModel]] = []
            
            // 遍历服务器返回字典
            for (_, value) in dict {
                // 若当前项为数组（每天的课时数组），执行遍历
                if let array = value as? [AnyObject] {
                    // 遍历课时数组
                    var tempArray: [ClassScheduleDayModel] = []
                    for dictJson in array {
                        // 验证为字典，字典转模型并放入模型数组中
                        if let dict = dictJson as? [String: AnyObject] {
                            let object = ClassScheduleDayModel(dict: dict)
                            tempArray.append(object)
                        }
                        
                    }
                    // 将课时模型数组，添加到结果数组中
                    modelArray.append(tempArray)
                }
            }
            self?.classScheduleModel = modelArray
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
                    // 当户用选择不同的上课地点时，更新课程表视图
                    if school.schoolModel?.id != self?.choosingObject?.school?.id {
                        self?.loadClassSchedule()
                    }
                    
                    // 保存用户所选上课地点
                    self?.choosingObject?.school = school.schoolModel
                    
                    // 设置tableView 的数据源和选中项
                    self?.tableView.schoolModel = [school.schoolModel!]
                    self?.selectedSchoolIndexPath = school.selectedIndexPath!
                }
        }
        // 选择上课时间
        NSNotificationCenter.defaultCenter().addObserverForName(
            MalaNotification_ClassScheduleDidTap,
            object: nil,
            queue: nil) { [weak self] (notification) -> Void in
                print("ClassSchedule Did Tap")
                let model = notification.object as! ClassScheduleDayModel
                // 判断上课时间是否已经选择
                let index = self?.choosingObject?.selectedTime.indexOf(model)
                // 如果上课时间尚未选择，加入课程购买模型
                // 如果上课时间已经选择，从课程购买模型中移除
                if index == nil {
                    self?.choosingObject?.selectedTime.append(model)
                }else {
                    self?.choosingObject?.selectedTime.removeAtIndex(index!)
                }
                // 改变课时选择的基数，并刷新课时选择Cell
                // 课时基数最小为2
                let stepValue = Double((self?.choosingObject?.selectedTime.count ?? 1)*2)
                MalaClassPeriod_StepValue = stepValue == 0 ? 2 : stepValue
                print(MalaClassPeriod_StepValue)
                // 课时选择
                (self?.tableView.cellForRowAtIndexPath(NSIndexPath(forRow: 0, inSection: 3)) as? CourseChoosingClassPeriodCell)?.updateSetpValue()
                // 上课时间
                (self?.tableView.cellForRowAtIndexPath(NSIndexPath(forRow: 0, inSection: 4)) as? CourseChoosingTimeScheduleCell)?.timeScheduleResult = [
                    "2016/02/21 (08:00-10:00)",
                    "2016/02/21 (10:30-12:30)",
                    "2016/02/21 (15:30-17:30)",
                    "2016/02/23 (08:00-10:00)",
                    "2016/02/24 (10:30-12:30)",
                    "2016/02/28 (08:00-10:00)",
                    "2016/02/28 (10:30-12:30)",
                    "2016/02/28 (15:30-17:30)",
                    "2016/03/01 (08:00-10:00)",
                    "2016/03/02 (10:30-12:30)"
                ]
                self?.tableView.reloadSections(NSIndexSet(index: 4), withRowAnimation: .Fade)
        }
    }
    
    @objc private func popSelf() {
        self.navigationController?.popViewControllerAnimated(true)
    }
    
    deinit {
        print("choosing Controller deinit")
        NSNotificationCenter.defaultCenter().removeObserver(self, name: MalaNotification_ChoosingGrade, object: nil)
        NSNotificationCenter.defaultCenter().removeObserver(self, name: MalaNotification_OpenSchoolsCell, object: nil)
        NSNotificationCenter.defaultCenter().removeObserver(self, name: MalaNotification_ClassScheduleDidTap, object: nil)
    }
}


// MARK: - 课程购买模型
class CourseChoosingObject: NSObject {
    
    /// 授课年级
    var price: GradePriceModel?
    /// 上课地点
    var school: SchoolModel?
    /// 已选上课时间
    var selectedTime: [ClassScheduleDayModel] = []
    /// 上课小时数
    var classPeriod: Int = 2
}