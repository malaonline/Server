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
            println("时间表数据： \(model)")
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
        backgroundColor = UIColor.whiteColor()
        
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
        // 移除重用Cell中的临时label,样式
        for view in cell.contentView.subviews {
            if !(view is UIButton) {
                view.removeFromSuperview()
            }
        }
        cell.button.highlighted = false
        cell.button.selected = false
        
        // 设置Cell样式
        cell.contentView.layer.borderColor = MalaLoginVerifyButtonNormalColor.CGColor
        cell.contentView.layer.borderWidth = MalaScreenOnePixel
        
        
        // 在Cell中标注IndexPath
        let label = UILabel()
        label.font = UIFont.systemFontOfSize(12)
        label.center = cell.contentView.center
//        label.text = String(format: "%d-%d", indexPath.section, indexPath.item)
//        label.text = model.id
//        label.sizeToFit()
        cell.contentView.addSubview(label)
        
        // 设置Cell列头标题
        if indexPath.section == 0 {
            label.text = ThemeClassScheduleSectionTitles[indexPath.row]
            label.textColor = UIColor.whiteColor()
            label.sizeToFit()
            label.center = cell.contentView.center
            cell.button.highlighted = true
        }
        
        // 设置Cell行头标题
        if indexPath.row == 0 && indexPath.section > 0 && (model ?? []) != [] {
            
            label.removeFromSuperview()
            
            // 行头数据源
            let rowTitleModel = model?[0][indexPath.section-1]
            
            let firstLabel = UILabel()
            firstLabel.text = rowTitleModel?.start
            firstLabel.frame = CGRect(x: 0, y: 0, width: cell.frame.width, height: cell.frame.height/2)
            firstLabel.font = UIFont.systemFontOfSize(12)
            firstLabel.sizeToFit()
            firstLabel.frame.origin.x = (cell.frame.width - firstLabel.frame.width)/2
            firstLabel.frame.origin.y = cell.frame.height/2 - firstLabel.frame.height
            firstLabel.textColor = MalaDetailsCellSubTitleColor
            
            let secondLabel = UILabel()
            secondLabel.text = rowTitleModel?.end
            secondLabel.frame = CGRect(x: 0, y: cell.frame.height/2, width: cell.frame.width, height: cell.frame.height/2)
            secondLabel.font = UIFont.systemFontOfSize(12)
            secondLabel.sizeToFit()
            secondLabel.frame.origin.x = (cell.frame.width - secondLabel.frame.width)/2
            secondLabel.textColor = MalaDetailsCellSubTitleColor
            
            cell.contentView.addSubview(firstLabel)
            cell.contentView.addSubview(secondLabel)
        }
        
        // 根据数据源设置显示样式
        if indexPath.section > 0 && indexPath.row > 0 && (model ?? []) != [] {
//            label.removeFromSuperview()
            
            let itemModel = model?[indexPath.row-1][indexPath.section-1]
            
            label.text = String(format: "%d", itemModel?.id ?? 0)
            label.sizeToFit()
            
            cell.button.enabled = itemModel?.available ?? false
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
    
    // MARK: - Compontents
    lazy var button: UIButton = {
        let button = UIButton()
        button.setBackgroundImage(UIImage.withColor(UIColor.whiteColor()), forState: .Normal)
        button.setBackgroundImage(UIImage.withColor(MalaTeacherCellBackgroundColor), forState: .Disabled)
        button.setBackgroundImage(UIImage.withColor(MalaClassScheduleSelectedColor), forState: .Selected)
        button.setBackgroundImage(UIImage.withColor(MalaLoginVerifyButtonNormalColor), forState: .Highlighted)
        button.userInteractionEnabled = false
        return button
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
        contentView.addSubview(button)
        
        button.frame.size = contentView.frame.size
        button.center = contentView.center
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
        scrollDirection = .Vertical
        let itemWidth: CGFloat = frame.width / 8
        let itemHeight: CGFloat = frame.height / 6
        itemSize = CGSizeMake(itemWidth, itemHeight)
        minimumInteritemSpacing = 0
        minimumLineSpacing = 0
    }
}