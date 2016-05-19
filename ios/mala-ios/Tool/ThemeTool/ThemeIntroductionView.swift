//
//  ThemeIntroductionView.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/18.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

private let ThemeIntroductionViewCellReuseId = "ThemeIntroductionViewCellReuseId"

class ThemeIntroductionView: BaseViewController, UICollectionViewDelegate, UICollectionViewDataSource {

    // MARK: - Property
    /// 会员专享服务数据模型
    var model: [IntroductionModel] = MalaConfig.memberServiceData() {
        didSet {
            collectionView.reloadData()
        }
    }
    /// 当前下标
    var index: Int?

    
    // MARK: - Components
    /// 页数指示器
    private lazy var pageControl: PageControl = {
        let pageControl = PageControl()
        return pageControl
    }()
    /// 轮播视图
    private lazy var collectionView: UICollectionView = {
        let frame = CGRect(x: 0, y: 0, width: MalaScreenWidth, height: MalaScreenHeight-MalaScreenNaviHeight)
        let collectionView = UICollectionView(frame: CGRectZero, collectionViewLayout: ThemeIntroductionFlowLayout(frame: frame))
        return collectionView
    }()
    
    
    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUserInterface()
        configure()
        delay(0.05) {
            if let i = self.index {
                self.collectionView.scrollToItemAtIndexPath(NSIndexPath(forItem: i, inSection: 0), atScrollPosition: .None, animated: false)
                self.pageControl.setCurrentPage(CGFloat(i), animated: false)
            }
        }
    }
    
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    // MARK: - Private method
    private func configure() {
        collectionView.pagingEnabled = true
        collectionView.showsHorizontalScrollIndicator = false
        collectionView.showsVerticalScrollIndicator = false
        collectionView.delegate = self
        collectionView.dataSource = self
        collectionView.registerClass(ThemeIntroductionViewCell.self, forCellWithReuseIdentifier: ThemeIntroductionViewCellReuseId)
        
        pageControl.numberOfPages = model.count
        pageControl.addTarget(self, action: #selector(ThemeIntroductionView.pageControlDidChangeCurrentPage(_:)), forControlEvents: .ValueChanged)
    }
    
    private func setupUserInterface() {
        // Style
        collectionView.backgroundColor = UIColor.whiteColor()
        pageControl.tintColor = MalaColor_2AAADD_0
        
        // SubViews
        view.addSubview(collectionView)
        view.addSubview(pageControl)
        
        // Autolayout
        collectionView.snp_makeConstraints { (make) in
            make.center.equalTo(self.view.snp_center)
            make.size.equalTo(self.view.snp_size)
        }
        pageControl.snp_makeConstraints { (make) in
            make.width.equalTo(200)
            make.centerX.equalTo(self.view.snp_centerX)
            make.bottom.equalTo(self.view.snp_bottom).offset(-20)
            make.height.equalTo(10)
        }
    }
    
    
    // MARK: - DataSource
    func collectionView(collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return model.count
    }
    
    func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCellWithReuseIdentifier(ThemeIntroductionViewCellReuseId, forIndexPath: indexPath) as! ThemeIntroductionViewCell
        cell.model = self.model[indexPath.row]
        return cell
    }
    
    
    // MARK: - Delegate
    
    
    // MARK: - Event Response
    func pageControlDidChangeCurrentPage(pageControl: PageControl) {
        collectionView.setContentOffset(CGPoint(x: collectionView.bounds.width * CGFloat(pageControl.currentPage), y: 0), animated: true)
    }
    
    func scrollViewDidScroll(scrollView: UIScrollView) {
        if scrollView.dragging || scrollView.decelerating {
            let page = scrollView.contentOffset.x / scrollView.bounds.width
            pageControl.setCurrentPage(page)
        }
    }
}


class ThemeIntroductionViewCell: UICollectionViewCell {
    // MARK: - Property
    /// 会员专享模型
    var model: IntroductionModel? {
        didSet {
            imageView.image = UIImage(named: (model?.image ?? "")+"_detail")
            titleLabel.text = model?.title
            detailLabel.text = model?.subTitle
        }
    }
    
    
    // MARK: - Compontents
    /// 布局容器
    private lazy var layoutView: UIView = {
        let view = UIView()
        return view
    }()
    /// 图片
    private lazy var imageView: UIImageView = {
        let imageView = UIImageView()
        return imageView
    }()
    /// 标题标签
    private lazy var titleLabel: UILabel = {
        let label = UILabel(
            text: "简介标题",
            fontSize: 16,
            textColor: MalaColor_2AAADD_0
        )
        label.textAlignment = .Center
        return label
    }()
    /// 简介内容标签
    private lazy var detailLabel: UILabel = {
        let label = UILabel(
            text: "简介内容",
            fontSize: 14,
            textColor: MalaColor_2AAADD_0
        )
        label.numberOfLines = 0
        label.textAlignment = .Center
        return label
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
        contentView.addSubview(layoutView)
        layoutView.addSubview(imageView)
        layoutView.addSubview(titleLabel)
        layoutView.addSubview(detailLabel)
        
        // Autolayout
        layoutView.snp_makeConstraints { (make) in
            make.center.equalTo(self.contentView.snp_center)
            make.width.equalTo(self.contentView.snp_width)
            make.height.equalTo(self.contentView.snp_height).multipliedBy(0.75)
        }
        imageView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(layoutView.snp_top).offset(40)
            make.centerX.equalTo(self.contentView.snp_centerX)
            make.width.equalTo(217)
            make.height.equalTo(183)
        }
        titleLabel.snp_makeConstraints { (make) -> Void in
            make.centerX.equalTo(imageView.snp_centerX)
            make.height.equalTo(16)
            make.top.equalTo(imageView.snp_bottom).offset(30)
        }
        detailLabel.snp_makeConstraints { (make) in
            make.top.equalTo(titleLabel.snp_bottom).offset(25)
            make.centerX.equalTo(imageView.snp_centerX)
            make.width.equalTo(200)
        }
    }
}

class ThemeIntroductionFlowLayout: UICollectionViewFlowLayout {
    
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
        scrollDirection = .Horizontal
        itemSize = frame.size
        minimumInteritemSpacing = 0
        minimumLineSpacing = 0
    }
}