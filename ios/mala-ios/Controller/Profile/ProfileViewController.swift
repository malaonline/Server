//
//  ProfileViewController.swift
//  mala-ios
//
//  Created by Elors on 12/18/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

private let ProfileViewTableViewCellReuseID = "ProfileViewTableViewCellReuseID"
private let ProfileViewTableViewItemCellReuseID = "ProfileViewTableViewItemCellReuseID"

class ProfileViewController: UITableViewController, UIImagePickerControllerDelegate, UINavigationControllerDelegate, ProfileViewHeaderViewDelegate {
    
    // MARK: - Property
    /// [个人中心结构数据]
    private var model: [[ProfileElementModel]] = MalaConfig.profileData()
    
    
    // MARK: - Components
    /// [个人中心]头部视图
    private lazy var profileHeaderView: ProfileViewHeaderView = {
        let view = ProfileViewHeaderView(frame: CGRect(x: 0, y: 0, width: MalaScreenWidth, height: MalaLayout_ProfileHeaderViewHeight))
        view.name = MalaUserDefaults.studentName.value ?? "学生姓名"
        view.delegate = self
        return view
    }()
    /// [个人中心]底部视图
    private lazy var profileFooterView: UIView = {
        let view = UIView(frame: CGRect(x: 0, y: 0, width: 0, height: 55))
        return view
    }()
    /// 顶部背景图
    private lazy var headerBackground: UIImageView = {
        let image = UIImageView(image: UIImage(named: "profile_headerBackground"))
        image.contentMode = .ScaleAspectFill
        return image
    }()
    /// [退出登录] 按钮
    private lazy var logoutButton: UIButton = {
        let button = UIButton()
        button.layer.cornerRadius = 5
        button.layer.masksToBounds = true
        
        button.setTitle("退  出", forState: .Normal)
        button.setTitleColor(UIColor.whiteColor(), forState: .Normal)
        button.setBackgroundImage(UIImage.withColor(MalaColor_82B4D9_0), forState: .Normal)
        button.setBackgroundImage(UIImage.withColor(UIColor(rgbHexValue: 0x82B4D9, alpha: 0.6)), forState: .Highlighted)
        button.titleLabel?.font = UIFont.systemFontOfSize(16)
        
        button.addTarget(self, action: #selector(ProfileViewController.logoutButtonDidTap), forControlEvents: .TouchUpInside)
        return button
    }()
    /// 照片选择器
    private lazy var imagePicker: UIImagePickerController = {
        let imagePicker = UIImagePickerController()
        imagePicker.delegate = self
        imagePicker.allowsEditing = true
        return imagePicker
    }()
    
    

    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()

        println("UserToken is \(MalaUserDefaults.userAccessToken.value)")
        println("profileID is \(MalaUserDefaults.profileID.value)")
        println("parentID is \(MalaUserDefaults.parentID.value)")
        println("studentName is \(MalaUserDefaults.studentName.value)")
        configure()
        setupUserInterface()
        setupNotification()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        
        // 每次显示[个人]页面时，刷新个人信息
        model = MalaConfig.profileData()
        tableView.reloadData()
        profileHeaderView.refreshDataWithUserDefaults()
        self.navigationController?.showTabBadgePoint = MalaUnpaidOrderCount > 0
    }
    
    override func viewDidAppear(animated: Bool) {
        super.viewDidAppear(animated)
        sendScreenTrack(SAProfileViewName)
    }
    
    
    // MARK: - Private Method
    private func configure() {
                
        // register
        tableView.registerClass(ProfileViewCell.self, forCellReuseIdentifier: ProfileViewTableViewCellReuseID)
        tableView.registerClass(ProfileItemViewCell.self, forCellReuseIdentifier: ProfileViewTableViewItemCellReuseID)
    }
    
    private func setupUserInterface() {
        // Style
        tableView.tableHeaderView = profileHeaderView
        tableView.tableFooterView = profileFooterView
        tableView.backgroundColor = MalaColor_F2F2F2_0
        tableView.separatorStyle = .None
        
        // SubViews
        tableView.insertSubview(headerBackground, atIndex: 0)
        profileFooterView.addSubview(logoutButton)
        
        
        // Autolayout
        headerBackground.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(0)
            make.centerX.equalTo(self.tableView.snp_centerX)
            make.width.equalTo(MalaScreenWidth)
            make.height.equalTo(MalaLayout_ProfileHeaderViewHeight)
        }
        logoutButton.snp_makeConstraints { (make) -> Void in
            make.bottom.equalTo(profileFooterView.snp_bottom)
            make.centerX.equalTo(profileFooterView)
            make.width.equalTo(profileFooterView).multipliedBy(0.85)
            make.height.equalTo(37)
        }
    }
    
    private func setupNotification() {
        NSNotificationCenter.defaultCenter().addObserverForName(
            MalaNotification_PushProfileItemController,
            object: nil,
            queue: nil
        ) { [weak self] (notification) -> Void in
            if let model = notification.object as? ProfileElementModel, type = model.controller as? UIViewController.Type {
                
                // 若对应项被冻结，则点击无效
                if model.disabled, let message = model.disabledMessage {
                    self?.ShowTost(message)
                    return
                }
                
                let viewController = type.init()
                viewController.title = model.controllerTitle
                viewController.hidesBottomBarWhenPushed = true
                self?.navigationController?.pushViewController(viewController, animated: true)
            }
        }
    }
    
    ///  更新本地AvatarView的图片
    ///
    ///  - parameter completion: 完成闭包
    private func updateAvatar(completion:() -> Void) {
        if let avatarURLString = MalaUserDefaults.avatar.value {
            
            println("avatarURLString: \(avatarURLString)")

            profileHeaderView.avatarURL = avatarURLString
//            let avatarSize = MalaConfig.editProfileAvatarSize()
//            let avatarStyle: AvatarStyle = .RoundedRectangle(size: CGSize(width: avatarSize, height: avatarSize), cornerRadius: avatarSize * 0.5, borderWidth: 0)
//            let plainAvatar = PlainAvatar(avatarURLString: avatarURLString, avatarStyle: avatarStyle)
//            avatarImageView.navi_setAvatar(plainAvatar, withFadeTransitionDuration: avatarFadeTransitionDuration)
            
            completion()
        }
    }
    
    
    // MARK: - DataSource
    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return model.count
    }

    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return section == 0 ? 1 : model[section].count
    }
    
    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        switch indexPath.section {
        case 0:
            
            let cell = tableView.dequeueReusableCellWithIdentifier(ProfileViewTableViewItemCellReuseID, forIndexPath: indexPath) as! ProfileItemViewCell
            cell.model = model[0]
            return cell
            
        default:
            
            let cell = tableView.dequeueReusableCellWithIdentifier(ProfileViewTableViewCellReuseID, forIndexPath: indexPath) as! ProfileViewCell
            
            cell.model =  model[indexPath.section][indexPath.row]
            // Section的最后一个Cell隐藏分割线
            if (indexPath.row+1) == model[indexPath.section].count {
                cell.hideSeparator()
            }
            return cell
        }
    }
    
    
    // MARK: - Delegate
    override func tableView(tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let view = UIView(frame: CGRect(x: 0, y: 0, width: 0, height: 8))
        view.backgroundColor = MalaColor_F2F2F2_0
        return view
    }
    
    override func tableView(tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return section == 0 ? 0 : 12
    }
    
    override func tableView(tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return 0.01
    }
    
    override func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        return indexPath.section == 0 ? 114 : 44
    }
    
    override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        let cell = tableView.cellForRowAtIndexPath(indexPath) as! ProfileViewCell
        let model = cell.model
        
        // 若对应项被冻结，则点击无效
        if model.disabled {
            return
        }
        
        // 跳转到对应的ViewController
        if let type = model.controller as? UIViewController.Type {
            let viewController = type.init()
            viewController.title = model.controllerTitle
            viewController.hidesBottomBarWhenPushed = true
            self.navigationController?.pushViewController(viewController, animated: true)
        }
    }

    override func scrollViewDidScroll(scrollView: UIScrollView) {
        let displacement = scrollView.contentOffset.y
        
        // 向下滑动页面时，使顶部图片跟随放大
        if displacement < 0 && headerBackground.superview != nil{
            headerBackground.snp_updateConstraints(closure: { (make) -> Void in
                make.top.equalTo(0).offset(displacement)
                // 1.1为放大速率
                make.height.equalTo(MalaLayout_ProfileHeaderViewHeight + abs(displacement)*1.1)
            })
        }
    }
    
    ///  HeaderView修改姓名
    func nameEditButtonDidTap(sender: UIButton) {
        let window = InfoModifyViewWindow(contentView: UIView())
        window.show()
    }
    
    ///  HeaderView头像点击事件
    ///
    ///  - parameter sender: UIImageView对象
    func avatarViewDidTap(sender: UIImageView) {
        
        // 准备ActionSheet选择[拍照]或[选择照片]
        let alertController = UIAlertController(title: nil, message: nil, preferredStyle: .ActionSheet)
        
        // 设置Action - 选择照片
        let choosePhotoAction: UIAlertAction = UIAlertAction(title: "选择照片", style: .Default) { (action) -> Void in
        
            let openCameraRoll: ProposerAction = { [weak self] in
                
                guard UIImagePickerController.isSourceTypeAvailable(.PhotoLibrary) else {
                    self?.alertCanNotAccessCameraRoll()
                    return
                }
                
                if let strongSelf = self {
                    strongSelf.imagePicker.sourceType = .PhotoLibrary
                    strongSelf.presentViewController(strongSelf.imagePicker, animated: true, completion: nil)
                }
            }
            
            proposeToAccess(.Photos, agreed: openCameraRoll, rejected: {
                self.alertCanNotAccessCameraRoll()
            })
            
        }
        alertController.addAction(choosePhotoAction)
        
        // 设置Action - 拍照
        let takePhotoAction: UIAlertAction = UIAlertAction(title: "拍照", style: .Default) { (action) -> Void in
            
            let openCamera: ProposerAction = { [weak self] in
                
                guard UIImagePickerController.isSourceTypeAvailable(.Camera) else {
                    self?.alertCanNotOpenCamera()
                    return
                }
                
                if let strongSelf = self {
                    strongSelf.imagePicker.sourceType = .Camera
                    strongSelf.presentViewController(strongSelf.imagePicker, animated: true, completion: nil)
                }
            }
            
            proposeToAccess(.Camera, agreed: openCamera, rejected: {
                self.alertCanNotOpenCamera()
            })
            
        }
        alertController.addAction(takePhotoAction)
        
        // 设置Action - 取消
        let cancelAction: UIAlertAction = UIAlertAction(title: "取消", style: .Cancel) { action -> Void in
            self.dismissViewControllerAnimated(true, completion: nil)
        }
        alertController.addAction(cancelAction)
        
        // 弹出ActionSheet
        self.presentViewController(alertController, animated: true, completion: nil)
    }
    
    func imagePickerController(picker: UIImagePickerController, didFinishPickingImage image: UIImage, editingInfo: [String : AnyObject]?) {
        
        defer {
            dismissViewControllerAnimated(true, completion: nil)
        }
        
        // 开启头像刷新指示器
        profileHeaderView.refreshAvatar = true
        
        // 处理图片尺寸和质量
        let image = image.largestCenteredSquareImage().resizeToTargetSize(MalaConfig.avatarMaxSize())
        let imageData = UIImageJPEGRepresentation(image, MalaConfig.avatarCompressionQuality())
        
        if let imageData = imageData {
            
            updateAvatarWithImageData(imageData, failureHandler: { (reason, errorMessage) in
                
                defaultFailureHandler(reason, errorMessage: errorMessage)
                
                dispatch_async(dispatch_get_main_queue()) { [weak self] in
                    self?.profileHeaderView.refreshAvatar = false
                }
                
                }, completion: { newAvatarURLString in
                    dispatch_async(dispatch_get_main_queue()) {
                        getAndSaveProfileInfo()
                        dispatch_async(dispatch_get_main_queue()) { [weak self] in
                            self?.profileHeaderView.avatar = UIImage(data: imageData) ?? UIImage()
                            self?.profileHeaderView.refreshAvatar = false
                        }
                        println("newAvatarURLString: \(newAvatarURLString)")
                    }
            })
        }
    }
    
    
    
    // MARK: - Event Response
    @objc private func logoutButtonDidTap() {
        MalaAlert.confirmOrCancel(
            title: "麻辣老师",
            message: "确认退出当前账号吗？",
            confirmTitle: "退出登录",
            cancelTitle: "取消",
            inViewController: self,
            withConfirmAction: { () -> Void in
                
                unregisterThirdPartyPush()
                cleanCaches()
                MalaUserDefaults.cleanAllUserDefaults()
                
                if let appDelegate = UIApplication.sharedApplication().delegate as? AppDelegate {
                    appDelegate.switchToStart()
                }
                
            }, cancelAction: { () -> Void in
        })
    }
    
    deinit {
        NSNotificationCenter.defaultCenter().removeObserver(self, name: MalaNotification_PushProfileItemController, object: nil)
    }
}


class ProfileItemViewCell: UITableViewCell {
    
    // MARK: - Property
    var model: [ProfileElementModel]? {
        didSet {
            collectionView.model = model
        }
    }
    
    
    // MARK: - Components
    private lazy var collectionView: ProfileItemCollectionView = {
        let collectionView = ProfileItemCollectionView(frame: CGRect(x: 0, y: 0, width: MalaScreenWidth, height: 114), collectionViewLayout: CommonFlowLayout(type: .ProfileItem))
        return collectionView
    }()
    
    
    // MARK: - Instance Method
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        
        contentView.addSubview(collectionView)
        
        collectionView.snp_makeConstraints { (make) -> Void in
            make.center.equalTo(contentView)
            make.width.equalTo(MalaScreenWidth)
            make.height.equalTo(114)
        }
    }
}