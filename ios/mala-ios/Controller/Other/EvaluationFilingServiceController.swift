//
//  EvaluationFilingServiceController.swift
//  mala-ios
//
//  Created by 王新宇 on 2/18/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

private let EvaluationFilingServiceCellReuseId = "EvaluationFilingServiceCellReuseId"

class EvaluationFilingServiceController: BaseTableViewController {

    // MARK: - Property
    var introductions: [IntroductionModel]? {
        didSet{
            self.tableView.reloadData()
        }
    }
    
    
    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        
        configure()
        loadIntroductions()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        
    }
    
    
    // MARK: - Delegate
    override func tableView(tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return section == (introductions?.count ?? 0)-1 ? 0 : 8
    }
    
    override func tableView(tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 0
    }
    
    
    // MARK: - DataSource
    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return self.introductions?.count ?? 0
    }
    
    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 1
    }
    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier(EvaluationFilingServiceCellReuseId, forIndexPath: indexPath) as! EvaluationFilingServiceCell
        cell.selectionStyle = .None
        if self.introductions?[indexPath.section] != nil {
            cell.model = self.introductions![indexPath.section]
        }
        return cell
    }
    
    
    // MARK: - Private Method
    private func configure() {
        // Style 
        title = MalaCommonString_EvaluationFiling
        tableView.backgroundColor = UIColor.whiteColor()
        tableView.estimatedRowHeight = 300
        tableView.separatorStyle = .None
        
        self.tableView.registerClass(EvaluationFilingServiceCell.self, forCellReuseIdentifier: EvaluationFilingServiceCellReuseId)
    }
    
    // 读取 [测评建档]服务 简介
    private func loadIntroductions() {
        // 网络请求
        let dataArray = NSArray(contentsOfFile: NSBundle.mainBundle().pathForResource("EvaluationFiling.plist", ofType: nil)!) as? [AnyObject]
        var modelDicts: [IntroductionModel]? = []
        for object in dataArray! {
            if let dict = object as? [String: AnyObject] {
                let set = IntroductionModel(dict: dict)
                modelDicts?.append(set)
            }
        }
        self.introductions = modelDicts
    }
}


class EvaluationFilingServiceCell: MalaBaseCell {
    
    // MARK: - Property
    var model: IntroductionModel? {
        didSet {
            title = model?.title
            contentImageView.image = UIImage(named: (model?.image ?? ""))
            contentLabel.text = model?.subTitle
        }
    }
    
    
    // MARK: - Components
    /// 内容展示图片容器
    private lazy var contentImageView: UIImageView = {
        let contentImageView = UIImageView(image: UIImage(named: "detailPicture_placeholder"))
        return contentImageView
    }()
    /// 简介文本框
    private lazy var contentLabel: UILabel = {
        let contentLabel = UILabel()
        contentLabel.font = UIFont.systemFontOfSize(13)
        contentLabel.textColor = MalaColor_6C6C6C_0
        contentLabel.numberOfLines = 0
        return contentLabel
    }()
    
    
    // MARK: - Constructed
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
        
        // Subviews
        content.addSubview(contentImageView)
        content.addSubview(contentLabel)
        
        // Autolayout
        contentImageView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(content.snp_top)
            make.left.equalTo(content.snp_left)
            make.right.equalTo(content.snp_right)
            make.height.equalTo(contentImageView.snp_width).multipliedBy(0.47)
        }
        contentLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(contentImageView.snp_bottom).offset(14)
            make.left.equalTo(content.snp_left)
            make.right.equalTo(content.snp_right)
            make.bottom.equalTo(content.snp_bottom)
        }
    }
}