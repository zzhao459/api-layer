import { Component } from 'react';
import './Header.css';
import Link from '@mui/material/Link';

class Header extends Component {

    render() {
        return (
            <div className="header">
                <div className="product-name">
                    <Link href={"/ui/v1/certificate-service/#/dashboard"}>
                            <h1 >Certificate Service</h1>
                    </Link>
                </div>
            </div>
        );
    }
}

export default Header;



