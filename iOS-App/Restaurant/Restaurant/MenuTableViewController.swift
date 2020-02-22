//
//  MenuTableViewController.swift
//  Restaurant
//
//  Created by Sudhanshu Bhoi on 26/09/19.
//  Copyright © 2019 Sudhanshu Bhoi. All rights reserved.
//

import UIKit

class MenuTableViewController: UITableViewController {
    
    var category: String!
    var menuItems = [MenuItem]()

    override func viewDidLoad() {
        super.viewDidLoad()
        
        NotificationCenter.default.addObserver(self, selector: #selector(updateUI), name: MenuController.menuDataUpdatedNotification, object: nil)
        
        updateUI()
    }
    
    @objc func updateUI() {
        title = category.capitalized
        menuItems = MenuController.shared.items(forCategory: category) ?? []
        tableView.reloadData()
    }

    // MARK: - Table view data source

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return menuItems.count
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "MenuCellIdentifier", for: indexPath)
        configure(cell, forItemAt: indexPath)
        return cell
    }
    
    func configure(_ cell: UITableViewCell, forItemAt index: IndexPath) {
        let menuItem = menuItems[index.row]
        cell.textLabel?.text = menuItem.name
        cell.detailTextLabel?.text = String(format: "Rs.%.2f", menuItem.price)
        MenuController.shared.fetchImage(from: menuItem.imageURL) { (image) in
            guard let image = image else { return }
            DispatchQueue.main.sync {
                if let currentIndexPath = self.tableView.indexPath(for: cell), currentIndexPath != index {
                    return
                }
                
                cell.imageView?.image = image
                cell.setNeedsLayout()
            }
        }
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "MenuDetailSegue" {
            let menuItemViewController = segue.destination as! MenuItemViewController
            let index = tableView.indexPathForSelectedRow!.row
            menuItemViewController.menuItem = menuItems[index]
        }
    }
    
    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 50
    }
    
    override func encodeRestorableState(with coder: NSCoder) {
        super.encodeRestorableState(with: coder)
        coder.encode(category, forKey: "category")
    }
    
    override func decodeRestorableState(with coder: NSCoder) {
        super.decodeRestorableState(with: coder)
        
        category = coder.decodeObject(forKey: "category") as? String
        updateUI()
    }
}
