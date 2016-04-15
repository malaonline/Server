//
//  ThemeClassSchedule.swift
//  ThemeClassSchedule
//
//  Created by 王新宇 on 1/25/16.
//  Copyright © 2016 Elors. All rights reserved.
//

import UIKit

private let ThemeClassScheduleCellReuseId = "ThemeClassScheduleCellReuseId"
private let ThemeClassScheduleSectionTitles = ["时间", "周一", "周二", "周三", "周四", "周五", "周六", "周日"]


class ThemeClassSchedule: UICollectionView, UICollectionViewDelegate, UICollectionViewDataSource {

    // MARK: - Property
    var model: [[ClassScheduleDayModel]]? {
        didSet {
            reloadData()
        }
    }
    
    
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
        backgroundColor = MalaColor_88BCDE_0
        self.layer.borderColor = MalaColor_88BCDE_0.CGColor
        self.layer.borderWidth = 1
        
        registerClass(ThemeClassScheduleCell.self, forCellWithReuseIdentifier: ThemeClassScheduleCellReuseId)
    }
    
    
    // MARK: - DataSource
    func numberOfSectionsInCollectionView(collectionView: UICollectionView) -> Int {
        return 6
    }
    
    func collectionView(collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return 8
    }
    
    func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCellWithReuseIdentifier(ThemeClassScheduleCellReuseId, forIndexPath: indexPath) as! ThemeClassScheduleCell
        
        // 重置Cell样式
        cell.reset()
        
        // 设置Cell列头标题
        if indexPath.section == 0 {
            cell.title = ThemeClassScheduleSectionTitles[indexPath.row]
            cell.hiddenTitle = false
            cell.button.highlighted = true
        }
        
        // 设置Cell行头标题
        if indexPath.row == 0 && indexPath.section > 0 && (model ?? []) != [] {
            // 行头数据源
            let rowTitleModel = model?[0][indexPath.section-1]
            cell.start = rowTitleModel?.start
            cell.end = rowTitleModel?.end
            cell.hiddenTime = false
            cell.setNormal()
        }
        
        // 根据数据源设置显示样式
        if indexPath.section > 0 && indexPath.row > 0 && (model ?? []) != [] {
            let itemModel = model?[indexPath.row-1][indexPath.section-1]
            // 若不可选择 - disable
            cell.button.enabled = itemModel?.available ?? false
            // 若已选择的 - selected
            if itemModel?.isSelected != nil {
                cell.button.selected = itemModel!.isSelected
            }
        }
        return cell
    }
    
    // MARK: - Delegate
    func collectionView(collectionView: UICollectionView, didSelectItemAtIndexPath indexPath: NSIndexPath) {
        // 获取Cell对象
        let cell = collectionView.cellForItemAtIndexPath(indexPath) as! ThemeClassScheduleCell
        cell.button.selected = !cell.button.selected
        
        
        // 获取数据模型
        if self.model != nil && indexPath.row >= 1 && indexPath.section >= 1 {
            let model = self.model![indexPath.row-1][indexPath.section-1]
            println("点击model: \(model)")
            let weekID = (indexPath.row == 7 ? 0 : indexPath.row)
            model.weekID = weekID
            NSNotificationCenter.defaultCenter().postNotificationName(MalaNotification_ClassScheduleDidTap, object: model)
            model.isSelected = cell.button.selected
        }
    }
    
    func collectionView(collectionView: UICollectionView, shouldSelectItemAtIndexPath indexPath: NSIndexPath) -> Bool {
        // 不可点击表头、行头
        if indexPath.section > 0 && indexPath.row > 0 {
            let itemModel = model?[indexPath.row-1][indexPath.section-1]
            return itemModel?.available ?? false
        }
        return false
    }
}


class ThemeClassScheduleCell: UICollectionViewCell {
    
    // MARK: - Property
    /// 是否隐藏标题
    var hiddenTitle: Bool = true {
        didSet {
            titleLabel.hidden = hiddenTitle
        }
    }
    /// 是否隐藏每个时间段的开始时间、结束时间
    var hiddenTime: Bool = true {
        didSet {
            startLabel.hidden = hiddenTime
            endLabel.hidden = hiddenTime
        }
    }
    /// 标题文字
    var title: String = "" {
        didSet {
            titleLabel.text = title
        }
    }
    /// 开始时间文字
    var start: String? {
        didSet {
           startLabel.text = start
        }
    }
    /// 结束时间文字
    var end: String? {
        didSet {
            endLabel.text = end
        }
    }
    
    // MARK: - Compontents
    /// 多状态样式按钮，不进行用户交互
    lazy var button: UIButton = {
        let button = UIButton()
        button.setBackgroundImage(UIImage.withColor(UIColor.whiteColor()), forState: .Normal)
        button.setBackgroundImage(UIImage.withColor(MalaColor_EDEDED_0), forState: .Disabled)
        button.setBackgroundImage(UIImage.withColor(MalaColor_ABD0E8_0), forState: .Selected)
        button.setBackgroundImage(UIImage.withColor(MalaColor_88BCDE_0), forState: .Highlighted)
        button.userInteractionEnabled = false
        return button
    }()
    /// 标题label
    private lazy var titleLabel: UILabel = {
        let titleLabel = UILabel()
        titleLabel.font = UIFont.systemFontOfSize(12)
        titleLabel.textColor = UIColor.whiteColor()
        titleLabel.hidden = true
        titleLabel.textAlignment = .Center
        return titleLabel
    }()
    /// 第一个时间段label
    private lazy var startLabel: UILabel = {
        let startLabel = UILabel()
        startLabel.text = "00:00"
        startLabel.font = UIFont.systemFontOfSize(12)
        startLabel.textColor = MalaColor_939393_0
        startLabel.backgroundColor = UIColor.clearColor()
        startLabel.hidden = true
        startLabel.textAlignment = .Center
        return startLabel
    }()
    /// 第二个时间段label
    private lazy var endLabel: UILabel = {
        let endLabel = UILabel()
        endLabel.text = "00:00"
        endLabel.font = UIFont.systemFontOfSize(12)
        endLabel.textColor = MalaColor_939393_0
        endLabel.backgroundColor = UIColor.clearColor()
        endLabel.hidden = true
        endLabel.textAlignment = .Center
        return endLabel
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
        
        // SubViews
        contentView.addSubview(button)
        contentView.insertSubview(startLabel, aboveSubview: button)
        contentView.insertSubview(endLabel, aboveSubview: startLabel)
        contentView.insertSubview(titleLabel, aboveSubview: endLabel)
        
        // Autolayout
        button.snp_makeConstraints { (make) -> Void in
            make.size.equalTo(contentView.snp_size)
            make.center.equalTo(contentView.snp_center)
        }
        startLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(contentView.snp_top)
            make.left.equalTo(contentView.snp_left)
            make.right.equalTo(contentView.snp_right)
            make.height.equalTo(contentView.snp_height).multipliedBy(0.5)
        }
        endLabel.snp_makeConstraints { (make) -> Void in
            make.bottom.equalTo(contentView.snp_bottom)
            make.left.equalTo(contentView.snp_left)
            make.right.equalTo(contentView.snp_right)
            make.height.equalTo(contentView.snp_height).multipliedBy(0.5)
        }
        titleLabel.snp_makeConstraints { (make) -> Void in
            make.width.equalTo(contentView.snp_width)
            make.height.equalTo(12)
            make.center.equalTo(contentView.snp_center)
        }
    }
    
    ///  重置Cell外观样式（仅保留button显示normal状态）
    func reset() {
        self.setNormal()
        self.hiddenTitle = true
        self.hiddenTime = true
    }
    
    ///  设置button为normal状态
    func setNormal() {
        self.button.selected = false
        self.button.highlighted = false
        self.button.enabled = true
    }
}


class ThemeClassScheduleFlowLayout: UICollectionViewFlowLayout {
    
    private var frame = CGRectZero
    
    // MARK: - Instance Method
    init(frame: CGRect) {
        super.init()
        self.frame = frame
        
        configure()
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    
    // MARK: - Private Method
    private func configure() {
        let deviceName = UIDevice.currentDevice().modelName
        
        switch deviceName {
        case "iPhone 5", "iPhone 5s":
            
            scrollDirection = .Vertical
            let itemWidth: CGFloat = frame.width / 8 - 1
            let itemHeight: CGFloat = (frame.height-MalaScreenOnePixel) / 6 - 1
            itemSize = CGSizeMake(itemWidth, itemHeight)
            sectionInset = UIEdgeInsets(top: MalaScreenOnePixel, left: MalaScreenOnePixel, bottom: MalaScreenOnePixel, right: MalaScreenOnePixel)
            minimumInteritemSpacing = 1
            minimumLineSpacing = 1
            
            break
        case "iPhone 6", "iPhone 6s":
            
            scrollDirection = .Vertical
            let itemWidth: CGFloat = frame.width / 8 - 1
            let itemHeight: CGFloat = frame.height / 6 - 1
            itemSize = CGSizeMake(itemWidth, itemHeight)
            sectionInset = UIEdgeInsets(top: MalaScreenOnePixel, left: 0, bottom: MalaScreenOnePixel, right: 0)
            minimumInteritemSpacing = 1
            minimumLineSpacing = 1
            
            break
        case "iPhone 6 Plus", "iPhone 6s Plus":
            
            scrollDirection = .Vertical
            let itemWidth: CGFloat = frame.width / 8 - MalaScreenOnePixel*2
            let itemHeight: CGFloat = (frame.height+MalaScreenOnePixel) / 6 - MalaScreenOnePixel*2
            itemSize = CGSizeMake(itemWidth, itemHeight)
            sectionInset = UIEdgeInsets(top: MalaScreenOnePixel, left: MalaScreenOnePixel, bottom: MalaScreenOnePixel, right: MalaScreenOnePixel)
            minimumInteritemSpacing = MalaScreenOnePixel*2
            minimumLineSpacing = MalaScreenOnePixel*2
            
            break
        default:
            break
        }
    }
}