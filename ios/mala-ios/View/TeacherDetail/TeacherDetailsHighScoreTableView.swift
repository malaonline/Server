//
//  TeacherDetailsHighScoreTableView.swift
//  mala-ios
//
//  Created by Elors on 1/11/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

private let TeacherDetailsHighScoreTableViewCellReuseId = "TeacherDetailsHighScoreTableViewCellReuseId"

class TeacherDetailsHighScoreTableView: UITableView, UITableViewDelegate, UITableViewDataSource {

    // MARK: - Variables
    var model: [HighScoreModel] {
        didSet {
            reloadData()
        }
    }
    
    
    // MARK: - Constructed
    override init(frame: CGRect, style: UITableViewStyle) {
        self.model = []
        super.init(frame: frame, style: style)
        
        delegate = self
        dataSource = self
        registerClass(TeacherDetailsHighScoreTableViewCell.self, forCellReuseIdentifier: TeacherDetailsHighScoreTableViewCellReuseId)
        
        // Style
        scrollEnabled = false
        separatorStyle = .None
        
        setupTableHeaderView()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func setupTableHeaderView() {
        let headerView = TeacherDetailsHighScoreTableViewCell(style: .Default, reuseIdentifier: nil)
        headerView.setTableTitles(["姓  名", "提分区间", "所在学校", "考入学校"])
        self.tableHeaderView = headerView
    }
    
    
    // MARK: - DataSource
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return model.count
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier(TeacherDetailsHighScoreTableViewCellReuseId, forIndexPath: indexPath)
        (cell as! TeacherDetailsHighScoreTableViewCell).model = model[indexPath.row]
        return cell
    }
    
    func tableView(tableView: UITableView, shouldHighlightRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        return false
    }

    
    // MARK: - Delegate
    func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        return 33
    }
}

// MARK: - TeacherDetailsHighScoreTableViewCell
class TeacherDetailsHighScoreTableViewCell: UITableViewCell {
    
    // MARK: - Variables
    var model: HighScoreModel? {
        didSet {
            nameLabel.text = model!.name
            scoresLabel.text = String(format: "%d", model!.increased_scores ?? 0)
            schoolLabel.text = model!.school_name
            admittedLabel.text = model!.admitted_to
        }
    }
    
    
    // MARK: - Components
    private lazy var nameLabel: UILabel = UILabel.subTitleLabel()
    private lazy var scoresLabel: UILabel = UILabel.subTitleLabel()
    private lazy var schoolLabel: UILabel = UILabel.subTitleLabel()
    private lazy var admittedLabel: UILabel = UILabel.subTitleLabel()
    private lazy var separator: UIView = {
        let separatorLine = UIView()
        separatorLine.backgroundColor = MalaDetailsButtonBorderColor
        return separatorLine
    }()
    
    
    // MARK: - Constructed
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        self.frame = CGRect(x: 0, y: 0, width: MalaScreenWidth - (MalaLayout_Margin_6*2), height: 33)
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // SubView
        contentView.addSubview(nameLabel)
        contentView.addSubview(scoresLabel)
        contentView.addSubview(schoolLabel)
        contentView.addSubview(admittedLabel)
        contentView.addSubview(separator)
        
        // Autolayout
        nameLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.contentView.snp_top)
            make.bottom.equalTo(self.contentView.snp_bottom)
            make.left.equalTo(self.contentView.snp_left)
        }
        scoresLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.contentView.snp_top)
            make.left.equalTo(self.nameLabel.snp_right)
            make.width.equalTo(self.nameLabel.snp_width)
            make.height.equalTo(self.nameLabel.snp_height)
        }
        schoolLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.contentView.snp_top)
            make.left.equalTo(self.scoresLabel.snp_right)
            make.width.equalTo(self.scoresLabel.snp_width)
            make.height.equalTo(self.scoresLabel.snp_height)
        }
        admittedLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.contentView.snp_top)
            make.left.equalTo(self.schoolLabel.snp_right)
            make.width.equalTo(self.schoolLabel.snp_width)
            make.height.equalTo(self.schoolLabel.snp_height)
            make.right.equalTo(self.contentView.snp_right)
        }
        separator.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(self.contentView.snp_left)
            make.right.equalTo(self.contentView.snp_right)
            make.height.equalTo(MalaScreenOnePixel)
            make.bottom.equalTo(self.contentView.snp_bottom)
        }
    }
    
    
    // MARK: - API
    ///  根据传入的表头字符串数组，生成对应的表头
    ///
    ///  - parameter titles: 表头字符串数组
    func setTableTitles(titles: [String]) {
        nameLabel.text = titles[0]
        scoresLabel.text = titles[1]
        schoolLabel.text = titles[2]
        admittedLabel.text = titles[3]
        for view in self.contentView.subviews {
            (view as? UILabel)?.textColor = MalaDetailsCellTitleColor
        }
        self.contentView.backgroundColor = MalaDetailsBottomViewColor
    }
}
