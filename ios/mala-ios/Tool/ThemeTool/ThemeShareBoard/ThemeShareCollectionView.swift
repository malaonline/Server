//
//  ThemeShareCollectionView.swift
//  mala-ios
//
//  Created by 王新宇 on 16/8/23.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

private let ThemeShareCollectionViewCellReuseId = "ThemeShareCollectionViewCellReuseId"
private let ThemeShareCollectionViewSectionHeaderReuseId = "ThemeShareCollectionViewSectionHeaderReuseId"
private let ThemeShareCollectionViewSectionFooterReuseId = "ThemeShareCollectionViewSectionFooterReuseId"

class ThemeShareCollectionView: UICollectionView, UICollectionViewDelegate, UICollectionViewDataSource {
    
    // MARK: - Property
    /// 会员专享服务数据
    var model: [IntroductionModel] = MalaConfig.shareItems() {
        didSet {
            reloadData()
        }
    }
    /// 老师模型
    var teacherModel: TeacherDetailModel?
    
    
    // MARK: - Instance Method
    override init(frame: CGRect, collectionViewLayout layout: UICollectionViewLayout) {
        super.init(frame: frame, collectionViewLayout: layout)
        
        configure()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func configure() {
        delegate = self
        dataSource = self
        backgroundColor = UIColor.clearColor()
        bounces = false
        scrollEnabled = false
        
        registerClass(ThemeShareCollectionViewCell.self, forCellWithReuseIdentifier: ThemeShareCollectionViewCellReuseId)
    }
    
    func collectionView(collectionView: UICollectionView, didSelectItemAtIndexPath indexPath: NSIndexPath) {
        let index = indexPath.section*2+(indexPath.row)
        let model = self.model[index]
        
        ThemeShare.hideShareBoard { 
            
        }
        
        println("分享按钮点击事件 \n \(teacherModel?.shareText) \n \(teacherModel?.avatar) \n \(teacherModel?.shareURL)")
        
        // 创建分享参数
        let shareParames = NSMutableDictionary()
        
        shareParames.SSDKSetupShareParamsByText(teacherModel?.shareText,
                                                images : teacherModel?.avatar,
                                                url : teacherModel?.shareURL,
                                                title : "我在麻辣老师发现一位好老师！",
                                                type : SSDKContentType.WebPage)
        // 进行分享
        ShareSDK.share(model.sharePlatformType, parameters: shareParames) { (state : SSDKResponseState, userData : [NSObject : AnyObject]!, contentEntity :SSDKContentEntity!, error : NSError!) -> Void in
            switch state{
            case SSDKResponseState.Success:
                println("分享成功")
            case SSDKResponseState.Fail:
                println("分享失败,错误描述:\(error)")
            case SSDKResponseState.Cancel:
                println("分享取消")
            default:
                break
            }
        }
    }
    
    
    // MARK: - DataSource
    func numberOfSectionsInCollectionView(collectionView: UICollectionView) -> Int {
        return 1
    }
    
    func collectionView(collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return 2
    }
    
    func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCellWithReuseIdentifier(ThemeShareCollectionViewCellReuseId, forIndexPath: indexPath) as! ThemeShareCollectionViewCell
        let index = indexPath.section*2 + (indexPath.row)
        if index < model.count {
            cell.model = self.model[index]
        }
        return cell
    }
}


// MARK: - ThemeShareCollectionViewCell
class ThemeShareCollectionViewCell: UICollectionViewCell {
    
    // MARK: - Property
    /// 会员专享模型
    var model: IntroductionModel? {
        didSet {
            iconView.image = UIImage(named: model?.image ?? "")
            titleLabel.text = model?.title
        }
    }
    
    
    // MARK: - Compontents
    /// 图标
    private lazy var iconView: UIImageView = {
        let imageView = UIImageView()
        return imageView
    }()
    /// 标题标签
    private lazy var titleLabel: UILabel = {
        let label = UILabel(
            text: "",
            fontSize: 13,
            textColor: MalaColor_6C6C6C_0
        )
        label.textAlignment = .Center
        return label
    }()
    private lazy var background: UIView = {
        let view = UIView()
        view.backgroundColor = UIColor.whiteColor()
        view.layer.cornerRadius = 10
        view.layer.masksToBounds = true
        return view
    }()
    
    
    // MARK: - Instance Method
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // Style
        contentView.backgroundColor = UIColor.clearColor()
        
        // SubViews
        contentView.addSubview(background)
        background.addSubview(iconView)
        contentView.addSubview(titleLabel)
        
        // Autolayout
        background.snp_makeConstraints { (make) in
            make.top.equalTo(contentView)
            make.centerX.equalTo(contentView)
            make.height.equalTo(55)
            make.width.equalTo(55)
        }
        iconView.snp_makeConstraints { (make) -> Void in
            make.center.equalTo(background)
            make.width.equalTo(47)
            make.height.equalTo(47)
        }
        titleLabel.snp_makeConstraints { (make) -> Void in
            make.centerX.equalTo(contentView.snp_centerX)
            make.height.equalTo(13)
            make.top.equalTo(background.snp_bottom).offset(10)
            make.bottom.equalTo(contentView.snp_bottom)
        }
    }
}


class ThemeShareFlowLayout: UICollectionViewFlowLayout {
    
    // MARK: - Instance Method
    init(frame: CGRect) {
        super.init()
        configure()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func configure() {
        scrollDirection = .Vertical
        let itemWidth: CGFloat = MalaLayout_CardCellWidth / 2
        let itemHeight: CGFloat = 78
        itemSize = CGSizeMake(itemWidth, itemHeight)
        headerReferenceSize = CGSize(width: 300, height: MalaScreenOnePixel)
        minimumInteritemSpacing = 0
        minimumLineSpacing = 0
    }
}