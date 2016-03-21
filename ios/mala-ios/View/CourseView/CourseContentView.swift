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
            
        }
    }
    /// 课程面板集合
    var panels: [CourseInfoView] = []
    
    
    // MARK: - Components
    private lazy var pageControl: UIPageControl = {
        let pageControl = UIPageControl()
        pageControl.currentPage = 0
        pageControl.numberOfPages = 3
        pageControl.pageIndicatorTintColor = MalaColor_C7DEEE_0
        pageControl.currentPageIndicatorTintColor = MalaColor_82B4D9_0
        
        // 添加横线
        let view = UIView()
        pageControl.addSubview(view)
        view.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(pageControl.snp_left)
            make.right.equalTo(pageControl.snp_right)
            make.height.equalTo(MalaScreenOnePixel)
            make.centerY.equalTo(pageControl.snp_centerY)
        }
        view.backgroundColor = MalaColor_C7DEEE_0
        return pageControl
    }()
    
    
    // MARK: - Constructed
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        
        println("CourseContentView ddiset")
        configure()
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
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
        addSubview(pageControl)
        
        // Autolayout
        pageControl.snp_makeConstraints { (make) -> Void in
            make.width.equalTo(36)
            make.height.equalTo(6)
            make.bottom.equalTo(self.snp_bottom).offset(-MalaLayout_Margin_10)
            make.centerX.equalTo(self.snp_centerX)
        }
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
        println(scrollView.contentOffset)
    }
}