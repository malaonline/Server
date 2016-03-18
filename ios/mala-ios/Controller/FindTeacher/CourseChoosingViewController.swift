//
//  CourseChoosingViewController.swift
//  mala-ios
//
//  Created by 王新宇 on 1/22/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class CourseChoosingViewController: UIViewController, CourseChoosingConfirmViewDelegate {

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
            MalaCourseChoosingObject.school = schoolArray[0]
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
//        loadSchoolsData()
        loadClassSchedule()
        loadCoupons()
        loadUserEvaluatedStatus()
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
        spacer.width = -MalaLayout_Margin_12
        
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
        
        confirmView.delegate = self
        
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
        MalaNetworking.sharedTools.loadSchools{[weak self] (result, error) -> () in
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
            MalaCourseChoosingObject.school = tempArray[0]
        }
    }
    
    private func loadClassSchedule() {
        
        let teacherID = teacherModel?.id ?? 1
        let schoolID = MalaCourseChoosingObject.school?.id ?? 1
        
        getTeacherAvailableTimeInSchool(teacherID, schoolID: schoolID, failureHandler: { (reason, errorMessage) -> Void in
            defaultFailureHandler(reason, errorMessage: errorMessage)
            
            // 错误处理
            if let errorMessage = errorMessage {
                println("CourseChoosingViewController - getTeacherAvailableTimeInSchool Error \(errorMessage)")
            }
        },completion: { [weak self] (timeSchedule) -> Void in
                self?.classScheduleModel = timeSchedule
        })
    }
    
    private func loadCoupons() {
        ///  获取优惠券信息
        getCouponList({ (reason, errorMessage) -> Void in
            defaultFailureHandler(reason, errorMessage: errorMessage)
            
            // 错误处理
            if let errorMessage = errorMessage {
                println("CourseChoosingViewController - loadCoupons Error \(errorMessage)")
            }
        }, completion: { [weak self] (coupons) -> Void in
                println("优惠券列表 \(coupons)")
                MalaUserCoupons = coupons
                self?.selectDefalutCoupon()
            })
    }
    
    private func loadUserEvaluatedStatus() {
        ///  判断用户是否首次购买此学科课程
        isHasBeenEvaluatedWithSubject(MalaSubjectName[(teacherModel?.subject) ?? ""] ?? 0, failureHandler: { (reason, errorMessage) -> Void in
            defaultFailureHandler(reason, errorMessage: errorMessage)
            
            // 错误处理
            if let errorMessage = errorMessage {
                println("CourseChoosingViewController - loadUserEvaluatedStatus Error \(errorMessage)")
            }
        }, completion: { (bool) -> Void in
                println("用户是否首次购买此学科课程？ \(bool)")
                MalaIsHasBeenEvaluatedThisSubject = bool
        })
    }
    
    ///  选择默认奖学金
    private func selectDefalutCoupon() {
        // 获取第一个可用的优惠券，并添加到选课数据模型中
        let coupon = getFirstUnusedCoupon(MalaUserCoupons)
        MalaCourseChoosingObject.coupon = coupon
        
        // 将该优惠券模型赋值到[其他服务]数组中，以待显示
        MalaOtherService[0] = OtherServiceModel(title: (coupon?.name ?? "奖学金"), type: .Coupon, price: coupon?.amount, priceHandleType: .Discount, viewController: CouponViewController.self)
    }
    
    private func setupNotification() {
        // 授课年级选择
        let observerChoosingGrade = NSNotificationCenter.defaultCenter().addObserverForName(
            MalaNotification_ChoosingGrade,
            object: nil,
            queue: nil) { (notification) -> Void in
                let price = notification.object as! GradePriceModel
                
                // 保存用户所选课程
                if price != MalaCourseChoosingObject.gradePrice {
                    MalaCourseChoosingObject.gradePrice = price
                    
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
                    
                    // 当户用选择不同的上课地点时，更新课程表视图,清空所有选课条件
                    if school.schoolModel?.id != MalaCourseChoosingObject.school?.id {
                        
                        // 保存用户所选上课地点
                        MalaCourseChoosingObject.school = school.schoolModel
                        
                        self?.loadClassSchedule()
                        MalaCourseChoosingObject.refresh()
                        (self?.tableView.cellForRowAtIndexPath(NSIndexPath(forRow: 0, inSection: 3)) as? CourseChoosingClassPeriodCell)?.updateSetpValue()
                    }
                    
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
                
                // 若[所选上课时间]多于当前课时，则改变课时，并刷新课时选择Cell
                let selectedTimePeriod = MalaCourseChoosingObject.selectedTime.count*2
                if selectedTimePeriod > MalaCourseChoosingObject.classPeriod {
                    MalaCourseChoosingObject.classPeriod = selectedTimePeriod
                }
                
                // 课时选择
                (self?.tableView.cellForRowAtIndexPath(NSIndexPath(forRow: 0, inSection: 3)) as? CourseChoosingClassPeriodCell)?.updateSetpValue()
                // 上课时间
                if MalaCourseChoosingObject.selectedTime.count != 0 {
                    let array = ThemeDate.dateArray((MalaCourseChoosingObject.selectedTime), period: Int(MalaCourseChoosingObject.classPeriod))
                    self?.tableView.timeScheduleResult = array
                }else {
                    self?.tableView.timeScheduleResult = []
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
                    self?.tableView.timeScheduleResult = array
                }
        }
        self.observers.append(observerClassPeriodDidChange)
        // 展开/收起 上课时间表
        let observerOpenTimeScheduleCell = NSNotificationCenter.defaultCenter().addObserverForName(
            MalaNotification_OpenTimeScheduleCell,
            object: nil,
            queue: nil) { [weak self] (notification) -> Void in
                if let bool = notification.object as? Bool {
                    self?.tableView.isOpenTimeScheduleCell = bool
                }
        }
        self.observers.append(observerOpenTimeScheduleCell)
    }
    
    /// 设置订单模型
    private func setupOrderForm() {
        MalaOrderObject.teacher = (teacherModel?.id) ?? 0
        MalaOrderObject.school  = (MalaCourseChoosingObject.school?.id) ?? 0
        MalaOrderObject.grade = (MalaCourseChoosingObject.gradePrice?.grade?.id) ?? 0
        MalaOrderObject.subject = MalaSubjectName[(teacherModel?.subject) ?? ""] ?? 0
        MalaOrderObject.coupon = MalaCourseChoosingObject.coupon?.id ?? 0
        MalaOrderObject.hours = MalaCourseChoosingObject.classPeriod
        MalaOrderObject.weekly_time_slots = MalaCourseChoosingObject.selectedTime.map{ (model) -> Int in
            return model.id
        }
    }
    
    
    // MARK: - Event Response
    @objc private func popSelf() {
        self.navigationController?.popViewControllerAnimated(true)
    }
    
    
    // MARK: - Delegate
    func OrderDidconfirm() {
        
        // 设置订单模型
        setupOrderForm()
        
        // 跳转到支付页面
        let viewController = PaymentViewController()
        // 设置支付页面弹栈闭包（用于[课程被抢买]时的回调刷新选课条件）
        viewController.popAction = { [weak self] in
            self?.loadClassSchedule()
        }
        self.navigationController?.pushViewController(viewController, animated: true)
    }
    
    
    deinit {
        println("choosing Controller deinit")
        
        // 还原选课模型
        MalaIsHasBeenEvaluatedThisSubject = nil
        
        // 移除观察者
        for observer in observers {
            NSNotificationCenter.defaultCenter().removeObserver(observer)
            self.observers.removeAtIndex(0)
        }
    }
}