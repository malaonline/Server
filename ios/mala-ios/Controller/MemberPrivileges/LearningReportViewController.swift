//
//  LearningReportViewController.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/19.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

let LearningReportCellReuseId = [
    0: "LearningReportTitlePageCellReuseId",        // 学习报告封面
    1: "LearningReportHomeworkDataCellReuseId",     // 作业数据分析
    2: "LearningReportTopicDataCellReuseId",        // 题目数据分析
    3: "LearningReportKnowledgeCellReuseId",        // 知识点分析
    4: "LearningReportAbilityStructureCellReuseId", // 能力结构分析
    5: "LearningReportAbilityImproveCellReuseId"    // 提分点分析
]

class LearningReportViewController: BaseViewController, UICollectionViewDelegate, UICollectionViewDataSource {

    // MARK: - Property
    /// 会员专享服务数据模型
    var model: [IntroductionModel] = MalaConfig.memberServiceData() {
        didSet {
            collectionView.reloadData()
        }
    }
    /// 标记是否作为样本展示
    var sample: Bool = true
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
        let collectionView = UICollectionView(frame: CGRectZero, collectionViewLayout: LearningReportFlowLayout(frame: frame))
        return collectionView
    }()
    
    
    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUserInterface()
        configure()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    // MARK: - Private method
    private func configure() {
        title = sample ? "学习报告样本" : "学习报告"
        collectionView.pagingEnabled = true
        collectionView.showsHorizontalScrollIndicator = false
        collectionView.showsVerticalScrollIndicator = false
        collectionView.delegate = self
        collectionView.dataSource = self
        collectionView.registerClass(LearningReportTitlePageCell.self, forCellWithReuseIdentifier: LearningReportCellReuseId[0]!)
        collectionView.registerClass(LearningReportHomeworkDataCell.self, forCellWithReuseIdentifier: LearningReportCellReuseId[1]!)
        collectionView.registerClass(LearningReportTopicDataCell.self, forCellWithReuseIdentifier: LearningReportCellReuseId[2]!)
        collectionView.registerClass(LearningReportKnowledgeCell.self, forCellWithReuseIdentifier: LearningReportCellReuseId[3]!)
        collectionView.registerClass(LearningReportAbilityStructureCell.self, forCellWithReuseIdentifier: LearningReportCellReuseId[4]!)
        collectionView.registerClass(LearningReportAbilityImproveCell.self, forCellWithReuseIdentifier: LearningReportCellReuseId[5]!)
        
        pageControl.numberOfPages = LearningReportCellReuseId.count
        pageControl.addTarget(self, action: #selector(ThemeIntroductionView.pageControlDidChangeCurrentPage(_:)), forControlEvents: .ValueChanged)
    }
    
    private func setupUserInterface() {
        // Style
        collectionView.backgroundColor = MalaColor_F2F2F2_0
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
        return LearningReportCellReuseId.count
    }
    
    func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {
        let reuseCell = collectionView.dequeueReusableCellWithReuseIdentifier(LearningReportCellReuseId[indexPath.row]!, forIndexPath: indexPath) as! MalaBaseCardCell
        reuseCell.asSample = sample
        
        switch indexPath.row {
        case 0:
            let cell = reuseCell as! LearningReportTitlePageCell
            return cell
            
        case 1:
            let cell = reuseCell as! LearningReportHomeworkDataCell
            return cell
            
        case 2:
            let cell = reuseCell as! LearningReportTopicDataCell
            return cell
            
        case 3:
            let cell = reuseCell as! LearningReportKnowledgeCell
            return cell
            
        case 4:
            let cell = reuseCell as! LearningReportAbilityStructureCell
            return cell
            
        case 5:
            let cell = reuseCell as! LearningReportAbilityImproveCell
            return cell
            
        default:
            return reuseCell
        }
    }
    
    
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


class LearningReportFlowLayout: UICollectionViewFlowLayout {
    
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