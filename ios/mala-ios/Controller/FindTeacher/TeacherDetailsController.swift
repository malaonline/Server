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
]

private let TeacherDetailsCellTitle = [
    1: "教授年级",
    2: "风格标签",
    3: "提分榜",
    4: "个人相册",
    5: "特殊成就",
    6: "教学环境",
]

class TeacherDetailsController: BaseViewController, UIGestureRecognizerDelegate, UITableViewDelegate, UITableViewDataSource, SignupButtonDelegate {

    // MARK: - Property
    var teacherID: Int = 0
    var model: TeacherDetailModel = MalaConfig.defaultTeacherDetail() {
        didSet {
            MalaOrderOverView.avatarURL = model.avatar
            MalaOrderOverView.teacherName = model.name
            MalaOrderOverView.subjectName = model.subject
            MalaOrderOverView.teacher = model.id
            
            tableHeaderView.model = model
            tableView.reloadData()
        }
    }
    var schoolArray: [SchoolModel] = [SchoolModel(id: 0, name: "线下体验店", address: "----")] {
        didSet {
            // 刷新 [教学环境] Cell
            tableView.reloadSections(NSIndexSet(index: 5), withRowAnimation: .None)
        }
    }
    var isOpenSchoolsCell: Bool = false
    var isNavigationBarShow: Bool = false
    /// 必要数据加载完成计数
    private var requiredCount: Int = 0 {
        didSet {
            // [老师详情][上课地点][会员服务]三个必要数据加载完成才激活界面
            if requiredCount == 3 {
                ThemeHUD.hideActivityIndicator()
            }
        }
    }

    
    // MARK: - Components
    /// 主体TableView
    private lazy var tableView: UITableView = {
        let tableView = UITableView(frame: CGRectZero, style: .Grouped)
        tableView.contentInset = UIEdgeInsets(top: 0, left: 0, bottom: 8, right: 0)
        return tableView
    }()
    /// TableView头部视图
    private lazy var tableHeaderView: TeacherDetailsHeaderView = {
        let tableHeaderView = TeacherDetailsHeaderView(frame: CGRect(x: 0, y: 0, width: MalaScreenWidth, height: MalaLayout_DetailHeaderContentHeight+50))
        return tableHeaderView
    }()
    /// 顶部背景图
    private lazy var headerBackground: UIImageView = {
        let image = UIImageView(image: UIImage(named: "teacherDetailHeader_placeholder"))
        image.contentMode = .ScaleAspectFill
        return image
    }()
    /// 底部 [立即报名] 按钮
    private lazy var signupView: TeacherDetailsSignupView = {
        let signupView = TeacherDetailsSignupView(frame: CGRect(x: 0, y: 0,
            width: MalaScreenWidth, height: MalaLayout_DetailBottomViewHeight))
        return signupView
    }()
    
    
    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        
        ThemeHUD.showActivityIndicator()
        
        setupUserInterface()
        loadTeacherDetail()
        setupNotification()
        loadSchoolsData()
        
        // 激活Pop手势识别
        self.navigationController?.interactivePopGestureRecognizer?.delegate = self
        self.navigationController?.interactivePopGestureRecognizer?.enabled = true
    }
    
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        // 设置 NavigationBar 透明色
        // makeStatusBarWhite()
        self.navigationController?.navigationBarHidden = false
        navigationController?.navigationBar.setBackgroundImage(UIImage(), forBarMetrics: .Default)
        navigationController?.navigationBar.shadowImage = UIImage()
    }
    
    override func viewDidAppear(animated: Bool) {
        super.viewDidAppear(animated)
        if isNavigationBarShow {
            showBackground()
        }else {
            hideBackground()
        }
    }
    
    override func viewWillDisappear(animated: Bool) {
        super.viewWillDisappear(animated)
        navigationController?.navigationBar.setBackgroundImage(UIImage.withColor(UIColor.whiteColor()), forBarMetrics: .Default)
    }
    
    override func viewDidDisappear(animated: Bool) {
        super.viewDidDisappear(animated)
        navigationController?.navigationBar.shadowImage = nil
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // Style
        signupView.delegate = self
        tableView.estimatedRowHeight = 240
        tableView.backgroundColor = MalaColor_EDEDED_0
        tableView.separatorStyle = .None
        tableView.delegate = self
        tableView.dataSource = self
        tableView.tableHeaderView = tableHeaderView
        tableView.registerClass(TeacherDetailsSubjectCell.self, forCellReuseIdentifier: TeacherDetailsCellReuseId[0]!)
        tableView.registerClass(TeacherDetailsTagsCell.self, forCellReuseIdentifier: TeacherDetailsCellReuseId[1]!)
        tableView.registerClass(TeacherDetailsHighScoreCell.self, forCellReuseIdentifier: TeacherDetailsCellReuseId[2]!)
        tableView.registerClass(TeacherDetailsPhotosCell.self, forCellReuseIdentifier: TeacherDetailsCellReuseId[3]!)
        tableView.registerClass(TeacherDetailsCertificateCell.self, forCellReuseIdentifier: TeacherDetailsCellReuseId[4]!)
        tableView.registerClass(TeacherDetailsPlaceCell.self, forCellReuseIdentifier: TeacherDetailsCellReuseId[5]!)
        
        // SubViews
        view.addSubview(tableView)
        view.addSubview(signupView)
        tableView.insertSubview(headerBackground, atIndex: 0)
        
        // Autolayout
        tableView.snp_makeConstraints { (make) in
            make.top.equalTo(view.snp_top)
            make.left.equalTo(view.snp_left)
            make.right.equalTo(view.snp_right)
            make.bottom.equalTo(view.snp_bottom).offset(-MalaLayout_DetailBottomViewHeight)
        }
        headerBackground.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(0).offset(-MalaScreenNaviHeight)
            make.left.equalTo(view.snp_left)
            make.right.equalTo(view.snp_right)
            make.height.equalTo(MalaLayout_DetailHeaderContentHeight)
        }
        signupView.snp_makeConstraints(closure: { (make) -> Void in
            make.left.equalTo(view.snp_left)
            make.right.equalTo(view.snp_right)
            make.bottom.equalTo(view.snp_bottom)
            make.height.equalTo(MalaLayout_DetailBottomViewHeight)
        })
    }
    
    private func setupNotification() {
        NSNotificationCenter.defaultCenter().addObserverForName(
            MalaNotification_OpenSchoolsCell,
            object: nil,
            queue: nil
            ) { [weak self] (notification) -> Void in
            // 展开 [教学环境] Cell
                if let isOpen = notification.object as? Bool {
                    self?.isOpenSchoolsCell = isOpen
                    self?.tableView.reloadSections(NSIndexSet(index: 5), withRowAnimation: .Fade)
                }
        }
        NSNotificationCenter.defaultCenter().addObserverForName(
            MalaNotification_PushPhotoBrowser,
            object: nil,
            queue: nil
            ) { [weak self] (notification) -> Void in
                
                // push图片浏览器
                if let info = notification.object as? String where info == "browser" {
                    // 相册
                    let browser = MalaPhotoBrowser()
                    browser.imageURLs = self?.model.photo_set ?? []
                    self?.navigationController?.pushViewController(browser, animated: true)
                }
                
                /// 确保是imageView触发手势，且imageView图片存在
                if let imageView = notification.object as? UIImageView, originImage = imageView.image {
                    
                    let images = self?.model.photo_set?.map({ (imageURL) -> SKPhoto in
                        let image = SKPhoto.photoWithImageURL(imageURL)
                        image.shouldCachePhotoURLImage = true
                        return image
                    })
                    
                    /// 图片浏览器
                    let browser = SKPhotoBrowser(originImage: originImage, photos: images ?? [], animatedFromView: imageView)
                    browser.initializePageIndex(imageView.tag)
                    browser.displayAction = false
                    browser.displayBackAndForwardButton = false
                    browser.displayDeleteButton = false
                    browser.statusBarStyle = nil
                    browser.bounceAnimation = false
                    browser.navigationController?.navigationBarHidden = true
                    self?.navigationController?.presentViewController(browser, animated: true, completion: nil)
                }
        }
    }
    
    private func loadTeacherDetail() {
        MalaNetworking.sharedTools.loadTeacherDetail(self.teacherID, finished: {[weak self] (result, error) -> () in
            ThemeHUD.hideActivityIndicator()
            if error != nil {
                println("TeahcerDeatilsController - loadTeacherDetail Request Error")
                return
            }
            guard let dict = result as? [String: AnyObject] else {
                println("TeahcerDeatilsController - loadTeacherDetail Format Error")
                return
            }
            self?.model = TeacherDetailModel(dict: dict)
            self?.requiredCount += 1
        })
    }
    
    private func loadSchoolsData() {
        // // 获取 [教学环境] 数据
        MalaNetworking.sharedTools.loadSchools{[weak self] (result, error) -> () in
            ThemeHUD.hideActivityIndicator()
            if error != nil {
                println("TeacherDetailsController - loadSchools Request Error")
                return
            }
            guard let dict = result as? [String: AnyObject] else {
                println("TeacherDetailsController - loadSchools Format Error")
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
            self?.requiredCount += 1
        }
    }
    
    private func showBackground() {
        // makeStatusBarBlack()
        title = model.name
        navigationController?.navigationBar.setBackgroundImage(UIImage.withColor(UIColor.whiteColor()), forBarMetrics: .Default)
        navigationController?.navigationBar.shadowImage = nil
        turnBackButtonBlack()
        isNavigationBarShow = true
    }
    
    private func hideBackground() {
        // makeStatusBarWhite()
        title = ""
        navigationController?.navigationBar.setBackgroundImage(UIImage.withColor(UIColor.clearColor()), forBarMetrics: .Default)
        navigationController?.navigationBar.shadowImage = UIImage()
        turnBackButtonWhite()
        isNavigationBarShow = false
    }
    
    // 跳转到课程购买页
    private func pushToCourseChoosingView() {
        let viewController = CourseChoosingViewController()
        viewController.teacherModel = model
        navigationController?.pushViewController(viewController, animated: true)
    }
    
    
    // MARK: - Deleagte
    func signupButtonDidTap(sender: UIButton) {
        
        // 未登陆则进行登陆动作
        if !MalaUserDefaults.isLogined {
            
            let loginController = LoginViewController()
            loginController.popAction = { [weak self] in
                if MalaUserDefaults.isLogined {
                    self?.pushToCourseChoosingView()
                }
            }
            
            self.presentViewController(
                UINavigationController(rootViewController: loginController),
                animated: true,
                completion: nil)
        
        }else {
            self.pushToCourseChoosingView()
        }
    }
    
    func scrollViewDidScroll(scrollView: UIScrollView) {
        let displacement = scrollView.contentOffset.y
        
        // 向下滑动页面时，使顶部图片跟随放大
        if displacement < -MalaScreenNaviHeight {
            headerBackground.snp_updateConstraints(closure: { (make) -> Void in
                make.top.equalTo(0).offset(displacement)
                // 1.1为放大速率
                make.height.equalTo(MalaLayout_DetailHeaderContentHeight + abs(displacement+MalaScreenNaviHeight)*1.1)
            })
        }
        
        // 上下滑动页面到一定程度且 NavigationBar 尚未显示，渲染NavigationBar样式
        if displacement > -40 && !isNavigationBarShow {
            showBackground()
        }
        if displacement < -40 && isNavigationBarShow {
            hideBackground()
        }
    }
    
    func tableView(tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 0.01
    }
    
    func tableView(tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return 0.01
    }
    
    func tableView(tableView: UITableView, shouldHighlightRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        return false
    }
    
    
    // MARK: - DataSource
    func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return TeacherDetailsCellReuseId.count
    }
    
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 1
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        
        let reuseCell = tableView.dequeueReusableCellWithIdentifier(TeacherDetailsCellReuseId[indexPath.section]!, forIndexPath: indexPath)
        (reuseCell as! TeacherDetailBaseCell).titleLabel.text = TeacherDetailsCellTitle[indexPath.section+1]
        
        switch indexPath.section {
        case 0:
            
            let cell = reuseCell as! TeacherDetailsSubjectCell
            cell.gradeStrings = model.grades
            return cell
            
        case 1:
            let cell = reuseCell as! TeacherDetailsTagsCell
             cell.labels = model.tags
            return cell
            
        case 2:
            let cell = reuseCell as! TeacherDetailsHighScoreCell
            cell.model = model.highscore_set
            return cell
            
        case 3:
            let cell = reuseCell as! TeacherDetailsPhotosCell
            cell.photos = model.photo_set ?? []
            return cell
            
        case 4:
            let cell = reuseCell as! TeacherDetailsCertificateCell
            cell.models = model.achievement_set
            return cell
            
        case 5:
            let cell = reuseCell as! TeacherDetailsPlaceCell
            cell.schools = schoolArray
            cell.isOpen = isOpenSchoolsCell
            return cell
            
        default:
            break
        }
        return reuseCell
    }


    deinit {
        println("TeacherDetailController Deinit")
        // 移除观察者
        NSNotificationCenter.defaultCenter().removeObserver(self, name: MalaNotification_OpenSchoolsCell, object: nil)
        NSNotificationCenter.defaultCenter().removeObserver(self, name: MalaNotification_PushPhotoBrowser, object: nil)
    }
}