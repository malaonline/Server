//
//  CourseContentView.swift
//  mala-ios
//
//  Created by 王新宇 on 3/21/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class CourseContentView: UIScrollView, UIScrollViewDelegate {

    // MARK: - Property
    /// 课程数据模型数组
    var models: [CourseModel] = [] {
        didSet {
            models = TestFactory.testCourseModels()
            self.setupCoursePanels()
            container?.pageControl.numberOfPages = models.count
        }
    }
    /// 课程面板集合
    var panels: [CourseInfoView] = []
    /// 父容器
    weak var container: CoursePopupWindow?
    
    
    // MARK: - Constructed
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        configure()
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func layoutSubviews() {
        contentSize = CGSize(width: MalaLayout_CourseContentWidth*CGFloat(panels.count), height: 0)
    }
    
    // MARK: - Private
    private func configure() {
        self.scrollEnabled = true
        self.pagingEnabled = true
        self.delegate = self
        self.bounces = true
        self.showsHorizontalScrollIndicator = false
    }
    
    private func setupUserInterface() {
        // Style
        backgroundColor = UIColor.whiteColor()
        
        // SubViews
        
        // Autolayout
    }
    
    ///  设置课程面板
    private func setupCoursePanels() {
        
        // 移除课程面板
        let _ = panels.map { (courseInfoView) -> () in
            courseInfoView.removeFromSuperview()
        }
        
        // 遍历课程数据生成面板
        for (index, course) in models.enumerate() {
            
            let view = CourseInfoView()
            view.model = course
            addSubview(view)
            
            let x = MalaLayout_CourseContentWidth*CGFloat(index)
            
            view.snp_makeConstraints(closure: { (make) -> Void in
                make.top.equalTo(self.snp_top)
                make.bottom.equalTo(self.snp_bottom)
                make.left.equalTo(x)
                make.width.equalTo(MalaLayout_CourseContentWidth)
            })
            
            panels.append(view)
            contentSize = CGSize(width: MalaLayout_CourseContentWidth*CGFloat(panels.count), height: 0)
        }
    }
    
    
    // MARK: - Delegate
    func scrollViewDidScroll(scrollView: UIScrollView) {
        
        // 当前页数
        let currentPage = Int(floor((scrollView.contentOffset.x - MalaLayout_CourseContentWidth / 2) / MalaLayout_CourseContentWidth))+2
        println("当前页数:\(currentPage)")
        container?.pageControl.currentPage = Int(currentPage)
    }
}