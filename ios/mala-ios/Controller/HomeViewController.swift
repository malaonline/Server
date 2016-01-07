//
//  HomeViewController.swift
//  mala-ios
//
//  Created by Elors on 12/18/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

private let HomeViewCellReusedId = "HomeViewCellReusedId"

class HomeViewController: UICollectionViewController, DropViewDelegate {
    
    private lazy var teachers: [TeacherModel]? = nil
    private lazy var dropView: DropView = {
        let filterView = TeacherFilterView(frame: CGRectZero, collectionViewLayout: CommonFlowLayout(type: .FilterView))
        let dropView = DropView(frame: CGRect(x: 0, y: 64-MalaContentHeight, width: MalaScreenWidth, height: MalaContentHeight), viewController: self, contentView: filterView)
        dropView.delegate = self
        return dropView
    }()
    
    
    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        
        loadTeachers()
        
        // Register Cell Class
        self.collectionView!.registerClass(TeacherCollectionViewCell.self, forCellWithReuseIdentifier: HomeViewCellReusedId)
        
        setupUserInterface()
    }
    
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        navigationController?.navigationBar.setBackgroundImage(UIImage.withColor(UIColor.redColor()), forBarMetrics: .Default)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    
    // MARK: - Event
    @objc private func locationButtonDidClick() {
//        let alertView = UIAlertView.init(title: nil, message: "目前只支持洛阳地区，其他地区正在拓展中", delegate: nil, cancelButtonTitle: "知道了")
//        alertView.show()
        self.navigationController?.presentViewController(LoginViewController(), animated: true, completion: { () -> Void in
            
        })
    }
    
    @objc private func screeningButtonDidClick() {
        dropView.isShow ? dropView.dismiss() : dropView.show()
    }
    
    
    // MARK: - Consturcted
    init() {
        super.init(collectionViewLayout: CommonFlowLayout(type: .HomeView))
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - DataSource
    override func collectionView(collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return self.teachers?.count ?? 0
    }
    
    override func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCellWithReuseIdentifier(HomeViewCellReusedId, forIndexPath: indexPath) as! TeacherCollectionViewCell
        
        // Configure the cell
        //cell.backgroundColor = UIColor.lightGrayColor()
        cell.model = teachers![indexPath.row]
        
        return cell
    }
    
    
    // MARK: - Delegate
    override func collectionView(collectionView: UICollectionView, shouldHighlightItemAtIndexPath indexPath: NSIndexPath) -> Bool {
        return true
    }

    override func collectionView(collectionView: UICollectionView, shouldSelectItemAtIndexPath indexPath: NSIndexPath) -> Bool {
        return true
    }
    
    override func collectionView(collectionView: UICollectionView, didSelectItemAtIndexPath indexPath: NSIndexPath) {
        
        let teacherId = (collectionView.cellForItemAtIndexPath(indexPath) as! TeacherCollectionViewCell).model!.id
        
        // Request Teacher Info
        NetworkTool.sharedTools.loadTeacherDetail(teacherId, finished: {[weak self] (result, error) -> () in
            
            // Error
            if error != nil {
                debugPrint("HomeViewController - loadTeacherDetail Request Error")
                return
            }
            
            // Make sure Dict not nil
            guard let dict = result as? [String: AnyObject] else {
                debugPrint("HomeViewController - loadTeacherDetail Format Error")
                return
            }
            
            let viewController = TeacherDetailsController(style: .Grouped)
            
            // TODO: set Test Model
            let model = TeacherDetailModel()
            model.id = 1
            model.avatar = "https://s3.cn-north-1.amazonaws.com.cn/dev-upload/avatars/img0_68YMpeT.jpg?X-Amz-Credential=AKIAP22CWKUZDOMHLFGA%2F20160107%2Fcn-north-1%2Fs3%2Faws4_request&X-Amz-SignedHeaders=host&X-Amz-Expires=3600&X-Amz-Signature=27a93b2d31f80809c5c1f54ff3f4b9855dea3bfd3593921a1fdcab571ef969cd&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20160107T025154Z"
            model.gender = "fm"
            model.name = "丁思甜"
            model.degree = "s"
            model.teaching_age = 27
            model.level = "麻辣合伙人"
            model.subject = "数学"
            model.grades = ["小升初", "初中"]
            model.tags = ["幽默", "亲切", "专治厌学"]
            model.photo_set = [
                "https://s3.cn-north-1.amazonaws.com.cn/dev-upload/avatars/img0_68YMpeT.jpg?X-Amz-Credential=AKIAP22CWKUZDOMHLFGA%2F20160107%2Fcn-north-1%2Fs3%2Faws4_request&X-Amz-SignedHeaders=host&X-Amz-Expires=3600&X-Amz-Signature=27a93b2d31f80809c5c1f54ff3f4b9855dea3bfd3593921a1fdcab571ef969cd&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20160107T025154Z",
                "https://s3.cn-north-1.amazonaws.com.cn/dev-upload/avatars/img1_YHYtvWX.jpg?X-Amz-Credential=AKIAP22CWKUZDOMHLFGA%2F20160107%2Fcn-north-1%2Fs3%2Faws4_request&X-Amz-SignedHeaders=host&X-Amz-Expires=3600&X-Amz-Signature=b0001bc1133bb6229d60b9b99a413bb2bfbf15537215ff30a141ddbcce3b614e&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20160107T025154Z",
                "https://s3.cn-north-1.amazonaws.com.cn/dev-upload/avatars/img2_mOkzmQw.jpg?X-Amz-Credential=AKIAP22CWKUZDOMHLFGA%2F20160107%2Fcn-north-1%2Fs3%2Faws4_request&X-Amz-SignedHeaders=host&X-Amz-Expires=3600&X-Amz-Signature=7bb0761fa15a491ed437588f81557938ba29d0b49b35f31d61f063f37c24c866&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20160107T025154Z"]
            model.certificate_set = ["特级教师","一级教师","十佳青年"]
            model.highscore_set = [
                HighScoreModel(name: "高明", score: 122, school: "洛阳一中", admitted: "河北大学"),
                HighScoreModel(name: "高晓明", score: 125, school: "洛阳二中", admitted: "北京大学"),
                HighScoreModel(name: "ElorsAt", score: 163, school: "洛阳三中", admitted: "中央美院"),
            ]
            model.prices = [
                GradePriceModel(name: "小学", id: 4121, price: 99),
                GradePriceModel(name: "初中基础一对一", id: 2133, price: 199),
                GradePriceModel(name: "高中数学", id: 4241, price: 399)
            ]
            
            
            
            viewController.model = model   // TeacherDetailModel(dict: dict)
            
            self?.navigationItem.backBarButtonItem = UIBarButtonItem(barButtonSystemItem: .Cancel, target: nil, action: nil)
            self?.navigationController?.pushViewController(viewController, animated: true)
        })
        
    }
    
    func dropViewDidTapButtonForContentView(contentView: UIView) {
        // get condition
        let filterObj: ConditionObject = (contentView as! TeacherFilterView).filterObject
        let filters: [String: AnyObject] = ["grade": filterObj.grade.id, "subject": filterObj.subject.id, "tags": filterObj.tag.id]
        loadTeachers(filters)
        dropView.dismiss()
    }
    
    
    // MARK: - private Method
    private func setupUserInterface() {
        
        collectionView?.backgroundColor = UIColor.whiteColor()
        
        // leftBarButtonItem
        navigationItem.leftBarButtonItem = UIBarButtonItem(customView: UIButton(title: "洛阳", imageName: "location_normal", target: self, action: "locationButtonDidClick"))
        // rightBarButtonItem
        navigationItem.rightBarButtonItem = UIBarButtonItem(customView: UIButton(imageName: "screening_normal", target: self, action: "screeningButtonDidClick"))
    }

    private func loadTeachers(filters: [String: AnyObject]? = nil) {
        NetworkTool.sharedTools.loadTeachers(filters) { result, error in
            // Error
            if error != nil {
                debugPrint("HomeViewController - loadTeachers Request Error")
                return
            }
            
            // Make sure Dict not nil
            guard let dict = result as? [String: AnyObject] else {
                debugPrint("HomeViewController - loadTeachers Format Error")
                return
            }

            self.teachers = []
            let resultModel = ResultModel(dict: dict)
            if resultModel.results != nil {
                for object in ResultModel(dict: dict).results! {
                    if let dict = object as? [String: AnyObject] {
                        self.teachers!.append(TeacherModel(dict: dict))
                    }
                }
            }
            self.collectionView?.reloadData()
        }
    }
    
}
