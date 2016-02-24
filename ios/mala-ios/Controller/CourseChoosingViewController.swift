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
    /// 上课地点Cell打开标识
    var isOpenSchoolsCell: Bool = false
    /// 当前上课地点记录下标
    var selectedSchoolIndexPath: NSIndexPath  = NSIndexPath(forRow: 0, inSection: 0)
    /// 观察者对象数组
    var observers: [AnyObject] = []
    
    
    
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
        NetworkTool.sharedTools.loadClassSchedule((teacherModel?.id ?? 1), schoolId: (MalaCourseChoosingObject.school?.id ?? 1)) {
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
        let observerChoosingGrade = NSNotificationCenter.defaultCenter().addObserverForName(
            MalaNotification_ChoosingGrade,
            object: nil,
            queue: nil) { (notification) -> Void in
                let price = notification.object as! GradePriceModel
                
                // 保存用户所选课程
                if price != MalaCourseChoosingObject.price {
                    MalaCourseChoosingObject.price = price
                    
                }
        }
        self.observers.append(observerChoosingGrade)
        // 选择上课地点
        let observerChoosingSchool = NSNotificationCenter.defaultCenter().addObserverForName(
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
                    if school.schoolModel?.id != MalaCourseChoosingObject.school?.id {
                        self?.loadClassSchedule()
                    }
                    
                    // 保存用户所选上课地点
                    MalaCourseChoosingObject.school = school.schoolModel
                    
                    // 设置tableView 的数据源和选中项
                    self?.tableView.schoolModel = [school.schoolModel!]
                    self?.selectedSchoolIndexPath = school.selectedIndexPath!
                }
        }
        self.observers.append(observerChoosingSchool)
        // 选择上课时间
        let observerClassScheduleDidTap = NSNotificationCenter.defaultCenter().addObserverForName(
            MalaNotification_ClassScheduleDidTap,
            object: nil,
            queue: nil) { [weak self] (notification) -> Void in
                let model = notification.object as! ClassScheduleDayModel
                
                // 判断上课时间是否已经选择
                let index = MalaCourseChoosingObject.selectedTime.indexOf(model)
                // 如果上课时间尚未选择，加入课程购买模型
                // 如果上课时间已经选择，从课程购买模型中移除
                if index == nil {
                    MalaCourseChoosingObject.selectedTime.append(model)
                }else {
                    MalaCourseChoosingObject.selectedTime.removeAtIndex(index!)
                }
                
                // 改变课时选择的基数，并刷新课时选择Cell
                // 课时基数最小为2
                let stepValue = Double((MalaCourseChoosingObject.selectedTime.count ?? 1)*2)
                MalaClassPeriod_StepValue = stepValue == 0 ? 2 : stepValue
                // 课时选择
                self?.tableView.isPeriodNeedUpdate = true
                (self?.tableView.cellForRowAtIndexPath(NSIndexPath(forRow: 0, inSection: 3)) as? CourseChoosingClassPeriodCell)?.updateSetpValue()
                // 上课时间
                if MalaCourseChoosingObject.selectedTime.count != 0 {
                    let array = ThemeDate.dateArray((MalaCourseChoosingObject.selectedTime), period: Int((MalaCourseChoosingObject.selectedTime.count)*2))
                    (self?.tableView.cellForRowAtIndexPath(NSIndexPath(forRow: 0, inSection: 4)) as? CourseChoosingTimeScheduleCell)?.timeScheduleResult = array
                    self?.tableView.timeScheduleResult = array
                    self?.tableView.reloadSections(NSIndexSet(index: 4), withRowAnimation: .Fade)
                }
        }
        self.observers.append(observerClassScheduleDidTap)
        // 选择课时
        let observerClassPeriodDidChange = NSNotificationCenter.defaultCenter().addObserverForName(
            MalaNotification_ClassPeriodDidChange,
            object: nil,
            queue: nil) { [weak self] (notification) -> Void in
                let period = (notification.object as? Double) ?? 2
                // 保存选择课时数
                MalaCourseChoosingObject.classPeriod = Int(period == 0 ? 2 : period)
                // 上课时间
                if MalaCourseChoosingObject.selectedTime.count != 0 {
                    let array = ThemeDate.dateArray(MalaCourseChoosingObject.selectedTime, period: Int(MalaCourseChoosingObject.classPeriod))
                    (self?.tableView.cellForRowAtIndexPath(NSIndexPath(forRow: 0, inSection: 4)) as? CourseChoosingTimeScheduleCell)?.timeScheduleResult = array
                    self?.tableView.timeScheduleResult = array
                    self?.tableView.reloadSections(NSIndexSet(index: 4), withRowAnimation: .Fade)
                }
        }
        self.observers.append(observerClassPeriodDidChange)
    }
    
    @objc private func popSelf() {
        self.navigationController?.popViewControllerAnimated(true)
    }
    
    deinit {
        print("choosing Controller deinit")
        for observer in observers {
            NSNotificationCenter.defaultCenter().removeObserver(observer)
            self.observers.removeAtIndex(0)
        }
    }
}


// MARK: - 课程购买模型
class CourseChoosingObject: NSObject {
    
    // MARK: - Property
    /// 授课年级
    dynamic var price: GradePriceModel? {
        didSet {
            originalPrice = getPrice()
        }
    }
    /// 上课地点
    dynamic var school: SchoolModel?
    /// 已选上课时间
    dynamic var selectedTime: [ClassScheduleDayModel] = [] {
        didSet {
            originalPrice = getPrice()
        }
    }
    /// 上课小时数
    dynamic var classPeriod: Int = 2 {
        didSet {
            originalPrice = getPrice()
        }
    }
    /// 原价
    dynamic var originalPrice: Int = 0
    
    
    // MARK: - API
    ///  根据当前选课条件获取价格, 选课条件不正确时返回0
    ///
    ///  - returns: 原价
    func getPrice() ->Int {
        if (price?.price != nil && selectedTime.count != 0 && classPeriod != 0) {
            return (price?.price)! * (selectedTime.count*2)
        }else {
            return 0
        }
    }
    
    ///  重置选课模型
    func reset() {
        price = nil
        school = nil
        selectedTime.removeAll()
        classPeriod = 2
    }
}