//
//  ModelController.swift
//  mala-ios
//
//  Created by Liang Sun on 11/6/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

/*
 A controller object that manages a simple model -- a collection of month names.
 
 The controller serves as the data source for the page view controller; it therefore implements pageViewController:viewControllerBeforeViewController: and pageViewController:viewControllerAfterViewController:.
 It also implements a custom method, viewControllerAtIndex: which is useful in the implementation of the data source methods, and in the initial configuration of the application.
 
 There is no need to actually create view controllers for each page in advance -- indeed doing so incurs unnecessary overhead. Given the data model, these methods create, configure, and return a new view controller on demand.
 */


class ModelController: NSObject, UIPageViewControllerDataSource {

    var pageData: [String] = []


    override init() {
        super.init()
        // Create the data model.
        let dateFormatter = NSDateFormatter()
        pageData = dateFormatter.monthSymbols
    }

    func viewControllerAtIndex(index: Int, storyboard: UIStoryboard) -> LoginViewController? {
        // Return the data view controller for the given index.
        if (self.pageData.count == 0) || (index >= self.pageData.count) {
            return nil
        }

        // Create a new view controller and pass suitable data.
//        let dataViewController = storyboard.instantiateViewControllerWithIdentifier("DataViewController") as! DataViewController
//        dataViewController.dataObject = self.pageData[index]
//        return dataViewController
        
        let loginViewController = storyboard.instantiateViewControllerWithIdentifier("LoginViewController") as! LoginViewController
        //loginViewController.dataObject = self.pageData[index]
        return loginViewController
    }

    func indexOfViewController(viewController: LoginViewController) -> Int {
        // Return the index of the given data view controller.
        // For simplicity, this implementation uses a static array of model objects and the view controller stores the model object; you can therefore use the model object to identify the index.
        //return pageData.indexOf(viewController.dataObject) ?? NSNotFound
        return NSNotFound
    }

    // MARK: - Page View Controller Data Source

    func pageViewController(pageViewController: UIPageViewController, viewControllerBeforeViewController viewController: UIViewController) -> UIViewController? {
        //var index = self.indexOfViewController(viewController as! DataViewController)
        var index = self.indexOfViewController(viewController as! LoginViewController)
        if (index == 0) || (index == NSNotFound) {
            return nil
        }
        
        index--
        return self.viewControllerAtIndex(index, storyboard: viewController.storyboard!)
    }

    func pageViewController(pageViewController: UIPageViewController, viewControllerAfterViewController viewController: UIViewController) -> UIViewController? {
        //var index = self.indexOfViewController(viewController as! DataViewController)
        var index = self.indexOfViewController(viewController as! LoginViewController)
        if index == NSNotFound {
            return nil
        }
        
        index++
        if index == self.pageData.count {
            return nil
        }
        return self.viewControllerAtIndex(index, storyboard: viewController.storyboard!)
    }

}

