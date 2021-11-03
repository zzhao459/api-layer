import { Component } from 'react';
import { TextField } from '@mui/material';
import './Header.css';

class Header extends Component {

    render() {
        return (
            <div className="header">
                <div className="product-name">
                        {/*<TextField element="h3" color="#ffffff">*/}
                            <h1 >Certificate Service</h1>
                        {/*</TextField>*/}
                </div>
            </div>
        );
    }
}

export default Header;



