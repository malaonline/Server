//
//  TeacherTableView.swift
//  mala-ios
//
//  Created by 王新宇 on 1/20/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

private let TeacherTableViewCellReusedId = "TeacherTableViewCellReusedId"

class TeacherTableView: UITableView, UITableViewDelegate, UITableViewDataSource {
    
    // MARK: - Property
    /// 老师数据模型数组
    var teachers: [TeacherModel]? {
        didSet {
            self.reloadData()
        }
    }
    var controller: UIViewController?
    
    
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
        backgroundColor = MalaTeacherCellBackgroundColor
        estimatedRowHeight = 200
        separatorStyle = .None
        contentInset = UIEdgeInsets(top: 4, left: 0, bottom: 4, right: 0)
        registerClass(TeacherTableViewCell.self, forCellReuseIdentifier: TeacherTableViewCellReusedId)
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
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.teachers?.count ?? 0
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier(TeacherTableViewCellReusedId, forIndexPath: indexPath) as! TeacherTableViewCell
        cell.selectionStyle = .None
        cell.model = teachers![indexPath.row]
        return cell
    }
    
    
    // MARK: - override
    override func reloadData() {
        super.reloadData()
        self.stopPullToRefresh()
    }
}
