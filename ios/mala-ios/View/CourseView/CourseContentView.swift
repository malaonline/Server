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
            if models.count == studentCourses.count {
                // 加载课程页面
                dispatch_async(dispatch_get_main_queue()) { [weak self] () -> Void in
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
        for studentCourse in studentCourses {
            
            getCourseInfo(studentCourse.id, failureHandler: { (reason, errorMessage) -> Void in
                defaultFailureHandler(reason, errorMessage: errorMessage)
                
                // 错误处理
                if let errorMessage = errorMessage {
                    println("CourseContentView - loadCourseInfo Error \(errorMessage)")
                }
                }, completion: { [weak self] (courseInfoModel) -> Void in
                    println("课程详细信息: \(courseInfoModel)")
                    self?.models.append(courseInfoModel)
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
        container?.pageControl.currentPage = Int(currentPage)
    }
}