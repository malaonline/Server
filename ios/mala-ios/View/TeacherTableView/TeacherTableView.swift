//
//  TeacherTableView.swift
//  mala-ios
//
//  Created by 王新宇 on 1/20/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

private let TeacherTableViewCellReusedId = "TeacherTableViewCellReusedId"
private let TeacherTableViewLoadmoreCellReusedId = "TeacherTableViewLoadmoreCellReusedId"

class TeacherTableView: UITableView, UITableViewDelegate, UITableViewDataSource {
    
    private enum Section: Int {
        case Teacher
        case LoadMore
    }
    
    // MARK: - Property
    /// 老师数据模型数组
    var teachers: [TeacherModel] = [] {
        didSet {
            self.reloadData()
        }
    }
    weak var controller: UIViewController?
    /// 上拉刷新视图
    private lazy var reloadView: ThemeReloadView = {
        let reloadView = ThemeReloadView(frame: CGRect(x: 0, y: 0, width: 0, height: 30))
        return reloadView
    }()
    
    
    // MARK: - Contructed
    override init(frame: CGRect, style: UITableViewStyle) {
        super.init(frame: frame, style: style)
        
        configration()
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func configration() {
        delegate = self
        dataSource = self
        backgroundColor = MalaColor_EDEDED_0
        estimatedRowHeight = 200
        separatorStyle = .None
        contentInset = UIEdgeInsets(top: 12, left: 0, bottom: 12, right: 0)
        
        registerClass(TeacherTableViewCell.self, forCellReuseIdentifier: TeacherTableViewCellReusedId)
        registerClass(ThemeReloadView.self, forCellReuseIdentifier: TeacherTableViewLoadmoreCellReusedId)
    }
    
    
    // MARK: - Delegate
    func tableView(tableView: UITableView, shouldHighlightRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        return true
    }
    
    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        let teacherId = (tableView.cellForRowAtIndexPath(indexPath) as! TeacherTableViewCell).model!.id
        let viewController = TeacherDetailsController()
        viewController.teacherID = teacherId
        viewController.hidesBottomBarWhenPushed = true
        controller?.navigationController?.pushViewController(viewController, animated: true)
    }
    
    
    // MARK: - DataSource
    func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 2
    }
    
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        switch section {
            
        case Section.Teacher.rawValue:
            return teachers.count ?? 0
            
        case Section.LoadMore.rawValue:
            if (controller as? FindTeacherViewController)?.allTeacherCount == teachers.count {
                return 0
            }else if (controller as? FilterResultController)?.allTeacherCount == teachers.count {
                return 0
            }else {
                return teachers.isEmpty ? 0 : 1
            }
            
        default:
            return 0
        }
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        
        switch indexPath.section {

        case Section.Teacher.rawValue:
            let cell = tableView.dequeueReusableCellWithIdentifier(TeacherTableViewCellReusedId, forIndexPath: indexPath) as! TeacherTableViewCell
            cell.selectionStyle = .None
            cell.model = teachers[indexPath.row]
            return cell
            
        case Section.LoadMore.rawValue:
            let cell = tableView.dequeueReusableCellWithIdentifier(TeacherTableViewLoadmoreCellReusedId, forIndexPath: indexPath) as! ThemeReloadView
            return cell
            
        default:
            return UITableViewCell()
        }
    }
    
    func tableView(tableView: UITableView, willDisplayCell cell: UITableViewCell, forRowAtIndexPath indexPath: NSIndexPath) {
        
        switch indexPath.section {
            
        case Section.Teacher.rawValue:
            break
            
        case Section.LoadMore.rawValue:
            if let cell = cell as? ThemeReloadView {
                println("load more Teacher info")
                
                if !cell.activityIndicator.isAnimating() {
                    cell.activityIndicator.startAnimating()
                }
                
                if let viewController = (controller as? FindTeacherViewController) {
                    viewController.loadTeachers(isLoadMore: true, finish: { [weak cell] in
                        cell?.activityIndicator.stopAnimating()
                        })
                    
                }else if let viewController = (controller as? FilterResultController) {
                    viewController.loadTeachers(isLoadMore: true, finish: { [weak cell] in
                        cell?.activityIndicator.stopAnimating()
                        })
                }
            }
            
        default:
            break
        }
    }
    
    
    // MARK: - override
    override func reloadData() {
        dispatch_async(dispatch_get_main_queue(), { () -> Void in
            super.reloadData()
        })
        self.stopPullToRefresh()
    }
}