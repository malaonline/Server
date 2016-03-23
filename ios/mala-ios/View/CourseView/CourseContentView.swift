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
            if models.count == studentCourses.count && models.count != 0 {
                
                // 数据排序
                models.sortInPlace({ (courseModel1, courseModel2) -> Bool in
                    return courseModel1.start < courseModel2.start
                })
                
                // 加载课程页面
                dispatch_async(dispatch_get_main_queue()) { [weak self] () -> Void in
                    
                    // 设置弹窗页面的[是否评价标记], 更改评价按钮样式
                    self?.container?.isComment = (self?.models[(self?.currentIndex) ?? 0].comment != nil)
                    
                    // 隐藏loading指示器
                    self?.activityIndicator.stopAnimating()
                    self?.scrollEnabled = true
                    self?.setupCoursePanels()
                }
            }
        }
    }
    /// 学生课程集合
    var studentCourses: [StudentCourseModel] = [] {
        didSet {

            self.loadCourseInfo()
            
            dispatch_async(dispatch_get_main_queue()) { [weak self] () -> Void in
                if self?.studentCourses.count <= 1 {
                    self?.container?.pageControl.hidden = true
                }else {
                    self?.container?.pageControl.numberOfPages = self?.studentCourses.count ?? 1
                }
                
            }
        }
    }
    /// 课程面板集合
    var panels: [CourseInfoView] = []
    /// 父容器
    weak var container: CoursePopupWindow?
    /// 当前页面下标
    private var currentIndex: Int = 0 {
        didSet {
            if currentIndex != oldValue {
                container?.pageControl.currentPage = currentIndex
                container?.isComment = (models[currentIndex].comment != nil)
                container?.isPassed = models[currentIndex].is_passed
                println("页面滑动 - 当前是否评价：\((models[currentIndex].comment != nil))")
            }
        }
    }
    /// 当前浏览课程
    var currentCourse: CourseModel {
        get {
            return self.models[currentIndex]
        }
    }
    
    
    // MARK: - Components
    /// loading指示器
    private lazy var activityIndicator: UIActivityIndicatorView = {
        let activityIndicator = UIActivityIndicatorView(activityIndicatorStyle: .Gray)
        activityIndicator.startAnimating()
        return activityIndicator
    }()
    
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
        self.scrollEnabled = false
        self.pagingEnabled = true
        self.delegate = self
        self.bounces = true
        self.showsHorizontalScrollIndicator = false
    }
    
    private func setupUserInterface() {
        // Style
        backgroundColor = UIColor.whiteColor()
        
        // SubViews
        addSubview(activityIndicator)
        
        // Autolayout
        activityIndicator.snp_makeConstraints { (make) -> Void in
            make.center.equalTo(self.snp_center)
        }
    }
    
    ///  根据课程id获取课程详细信息
    private func loadCourseInfo() {
        
        // 遍历学生课程数组，根据课程id获取课程详细信息
        for (index,studentCourse) in studentCourses.enumerate() {
            
            getCourseInfo(studentCourse.id, failureHandler: { (reason, errorMessage) -> Void in
                defaultFailureHandler(reason, errorMessage: errorMessage)
                
                // 错误处理
                if let errorMessage = errorMessage {
                    println("CourseContentView - loadCourseInfo Error \(errorMessage)")
                }
                }, completion: { [weak self] (courseInfoModel) -> Void in
                    self?.models.append(courseInfoModel)
                    println("课程详细信息: \(courseInfoModel) _ \(index)")
                })
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
        // 当前页数
        let currentPage = Int(floor((scrollView.contentOffset.x - MalaLayout_CourseContentWidth / 2) / MalaLayout_CourseContentWidth))+1
        currentIndex = currentPage
    }
}