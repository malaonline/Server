//
//  CourseChoosingGradeCell.swift
//  mala-ios
//
//  Created by 王新宇 on 1/22/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class CourseChoosingGradeCell: MalaBaseCell {
    
    // MARK: - Property
    var prices: [GradePriceModel?] = [] {
        didSet {
            self.collectionView.prices = prices
            var collectionRow = CGFloat(Int(prices.count ?? 0)/2)
            collectionRow = (prices.count)%2 == 0 ? collectionRow : collectionRow + 1
            let collectionHeight = (MalaLayout_GradeSelectionWidth*0.19) * collectionRow + (MalaLayout_Margin_14*(collectionRow-1))
            collectionView.snp_updateConstraints(closure: { (make) -> Void in
                make.height.equalTo(collectionHeight)
            })
        }
    }
    
    
    // MARK: - Compontents
    private lazy var collectionView: GradeSelectCollectionView = {
        let collectionView = GradeSelectCollectionView(
            frame: CGRect(x: 0, y: 0, width: MalaLayout_CardCellWidth, height: 0),
            collectionViewLayout: CommonFlowLayout(type: .GradeSelection)
        )
        return collectionView
    }()
    
    // MARK: - Contructed
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // Style
        self.title.text = "选择授课年级"
        
        // SubViews
        self.content.addSubview(collectionView)
        
        // Autolayout
        collectionView.snp_makeConstraints(closure: { (make) -> Void in
            make.top.equalTo(self.content.snp_top)
            make.left.equalTo(self.content.snp_left)
            make.right.equalTo(self.content.snp_right)
            make.bottom.equalTo(self.content.snp_bottom)
        })
    }
}


// MARK: - GradeSelectCollectionView
private let GradeSelectionCellReuseId = "GradeSelectionCellReuseId"
class GradeSelectCollectionView: UICollectionView, UICollectionViewDelegate, UICollectionViewDataSource {
    
    // MARK: - Property
    /// 年级价格数据模型
    var prices: [GradePriceModel?] = [] {
        didSet {
            self.reloadData()
        }
    }
    /// 当前选择项IndexPath标记
    private var currentSelectedIndexPath: NSIndexPath = NSIndexPath(forItem: 0, inSection: 0)
    
    
    // MARK: - Constructed
    override init(frame: CGRect, collectionViewLayout layout: UICollectionViewLayout) {
        super.init(frame: frame, collectionViewLayout: layout)
        configure()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Delegate
    func collectionView(collectionView: UICollectionView, didSelectItemAtIndexPath indexPath: NSIndexPath) {
        let cell = collectionView.cellForItemAtIndexPath(indexPath) as! GradeSelectionCell
        
        // 选中当前选择Cell，并取消其他Cell选择
        cell.selected = true
        (collectionView.cellForItemAtIndexPath(currentSelectedIndexPath)?.selected = false)
        currentSelectedIndexPath = indexPath
    }
    
    
    // MARK: - DataSource
    func collectionView(collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return prices.count ?? 0
    }
    
    func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCellWithReuseIdentifier(GradeSelectionCellReuseId, forIndexPath: indexPath) as! GradeSelectionCell
        cell.price = prices[indexPath.row]
        // 选中当前选择项
        if indexPath == currentSelectedIndexPath {
            cell.selected = true
            MalaOrderOverView.gradeName = cell.price?.grade?.name
        }
        return cell
    }
    
    
    // MARK: - Private Method
    private func configure() {
        dataSource = self
        delegate = self
        backgroundColor = UIColor.whiteColor()
        scrollEnabled = false
        
        registerClass(GradeSelectionCell.self, forCellWithReuseIdentifier: GradeSelectionCellReuseId)
    }
}


// MARK: - GradeSelectionCell
class GradeSelectionCell: UICollectionViewCell {
    
    // MARK: - Property
    var price: GradePriceModel? {
        didSet {
            let title = String(format: "%@  %@/小时", (price?.grade?.name)!, (price?.price.money)!)
            self.button.setTitle(title, forState: .Normal)
        }
    }
    override var selected: Bool {
        didSet {
            self.button.selected = selected
            if selected {
                NSNotificationCenter.defaultCenter().postNotificationName(MalaNotification_ChoosingGrade, object: self.price!)
            }
        }
    }
    
    // MARK: - Compontents
    private lazy var button: UIButton = {
        let button = UIButton(
            title: "年级——价格",
            borderColor: MalaColor_8FBCDD_0,
            target: self,
            action: nil
        )
        button.userInteractionEnabled = false
        return button
    }()
    
    // MARK: - Constructed
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // SubViews
        self.contentView.addSubview(button)
        
        // Autolayout
        button.snp_makeConstraints(closure: { (make) -> Void in
            make.top.equalTo(self.contentView.snp_top)
            make.left.equalTo(self.contentView.snp_left)
            make.right.equalTo(self.contentView.snp_right)
            make.bottom.equalTo(self.contentView.snp_bottom)
        })
    }
}