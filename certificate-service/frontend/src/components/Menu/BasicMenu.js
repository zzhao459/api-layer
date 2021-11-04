import * as React from 'react';
import Button from '@mui/material/Button';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import './BasicMenu.css';

export default function BasicMenu(props) {
    const [anchorEl, setAnchorEl] = React.useState(null);
    const [selectedIndex, setSelectedIndex] = React.useState(1);
    const open = Boolean(anchorEl);
    const handleClick = (event) => {
        props.changePanel(event.currentTarget.innerText);
        console.log(event.currentTarget.innerText)
        setAnchorEl(event.currentTarget);
    };

    const handleMenuItemClick = (event, index) => {
        props.changePanel(event.currentTarget.innerText);
        console.log(event.currentTarget.innerText)
        setSelectedIndex(index);
        setAnchorEl(null);
    };

    const options = [
        'Verify access to a service',
        'Verify the service is trusted',
        'Manage certificates',
    ];

    const handleClose = () => {
        setAnchorEl(null);
    };

    const handleClickListItem = (event) => {
        setAnchorEl(event.currentTarget);
    };

    return (
        <div className={"menu"}>
            <List
                component="nav"
                aria-label="Device settings"
            >
                <ListItem
                    button
                    id="lock-button"
                    aria-haspopup="listbox"
                    aria-controls="lock-menu"
                    aria-label="when device is locked"
                    aria-expanded={open ? 'true' : undefined}
                    onClick={handleClickListItem}
                >
                    <ListItemText
                        primary="Menu"
                        // secondary={options[selectedIndex]}
                    />
                </ListItem>
            </List>
            <Menu
                id="lock-menu"
                anchorEl={anchorEl}
                open={open}
                onClose={handleClose}
                MenuListProps={{
                    'aria-labelledby': 'lock-button',
                    role: 'listbox',
                }}
            >
                {options.map((option, index) => (
                    <MenuItem
                        key={option}
                        // disabled={index === 0}
                        selected={index === selectedIndex}
                        onClick={(event) => handleMenuItemClick(event, index)}
                    >
                        {option}
                    </MenuItem>
                ))}
            </Menu>
        </div>

            // </List>
            // <Button
            //     id="basic-button"
            //     aria-controls="basic-menu"
            //     aria-haspopup="true"
            //     aria-expanded={open ? 'true' : undefined}
            //     // onClick={handleClick}
            // >
            //     Menu
            // </Button>
            // <Menu
            //     id="basic-menu"
            //     anchorEl={anchorEl}
            //     open={open}
            //     onClose={handleClose}
            //     MenuListProps={{
            //         'aria-labelledby': 'basic-button',
            //     }}
            // >
            //     <MenuItem onClick={handleClick}>Verify access to a service</MenuItem>
            //     <MenuItem onClick={handleClick}>Verify the service is trusted</MenuItem>
            //     <MenuItem onClick={handleClick}>Manage certificates</MenuItem>
            // </Menu>
        // </div>
    );
}
